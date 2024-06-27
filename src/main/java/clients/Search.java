package clients;

import evaluation.GameEvaluator;
import exceptions.GamePhaseNotValidException;
import exceptions.NotEnoughTimeException;
import exceptions.OutOfTimeException;
import game.Community;
import game.Game;
import game.GamePhase;
import move.Move;
import util.Logger;
import util.Tuple;

import java.util.*;

import static util.Tree.calculateBranchingFactor;
import static util.Tree.calculateNodeCountOfTree;

public class Search {

    Logger logger = new Logger(this.getClass().getName());

    /**
     * The game for which to search a new move for.
     */
    private final Game game;

    /**
     * The number of the player to search a new move for.
     */
    private final int playerNumber;

    /**
     * The game evaluator used for this search.
     */
    private final GameEvaluator evaluator;

    /**
     * The timestamp in milliseconds at which we got a move request.
     */
    private long startTime;

    /**
     * The very latest time by which we should send a move.
     */
    private long endTime;

    /**
     * Stores for each timeout that occurred its stack trace.
     */
    private static final List<String> stats_timeouts = new LinkedList<>();

    /**
     * Stores for each tree layer that was successfully searched its depth.
     */
    private static final List<Integer> stats_depths = new LinkedList<>();

    /**
     * Counts how often we reached the bomb phase in the tree. Used for exiting the iterative
     * deepening search.
     */
    private int bombPhasesReached;

    /**
     * Initialize a new move search.
     * @param game         The game for which to search the best move.
     * @param playerNumber The player for which to search the best move.
     */
    public Search(Game game, int playerNumber, GameEvaluator evaluator) {
        this.game = game;
        this.playerNumber = playerNumber;
        this.evaluator = evaluator;
    }

    /**
     * Start the search.
     * @param timeLimit The maximum time after which a move has to be returned in milliseconds.
     * @return A valid move.
     */
    public Move search(int timeLimit) {

        this.startTime = System.currentTimeMillis();
        this.endTime = startTime + timeLimit;

        // Fallback
        Move result = game.getValidMoves().iterator().next();

        try {

            stats_depths.add(0);

            // For each depth we calculate the average time per game visited in order to
            // estimate a time for the next depth
            resetStats();

            bombPhasesReached = 0;

            // Iterative deepening search
            int depthLimit = 1;

            Set<Community> relevantCommunities = new HashSet<>();

            if (game.communities != null) {
                if (game.communities.getCommunities().size() < 2) {
                    logger.log("Disabling Communities as there is only one");
                    game.communities = null;
                }
            }

            if (game.communities != null) {
                if (evaluator.prepareMoves(game).stream().anyMatch(evaluator::isSpecialMove)) {
                    logger.log("Disabling Communities as we have special moves");
                    game.communities = null;
                }
            }

            if (game.communities != null) {
                relevantCommunities = game.communities.getRelevantCommunities(playerNumber);
                logger.log("Searching " + relevantCommunities.size() + " relevant Communities");
            }

            if (relevantCommunities.isEmpty()) {
                logger.log("Disabling Communities as there are no relevant Communities");
                game.communities = null;
            }

            if (game.communities == null) {
                logger.log("Searching whole game");
            }

            do {

                logger.log("Iterative deepening: Depth " + depthLimit);

                resetStats();

                if (game.communities != null) {
                    result = findBestMoveInCommunity(relevantCommunities, depthLimit);
                } else {
                    result = findBestMove(game, sortMoves(game), depthLimit).first();
                }

                stats_depths.add(depthLimit);

                if (bombPhasesReached >= game.getValidMoves().size()) {
                    throw new GamePhaseNotValidException("Tree reached bomb phase");
                }

                evaluateStats(depthLimit);

                depthLimit++;

            } while (game.getPhase().equals(GamePhase.BUILD));

            logger.log("Not going deeper as we are not in build phase");

        }
        catch (OutOfTimeException e) {
            String stackTrace = "at " + e.getStackTrace()[1].getMethodName() + " at " +
                    e.getStackTrace()[2].getMethodName();
            logger.warn(e.getMessage() + " " + stackTrace);
            stats_timeouts.add(stackTrace);
        }
        catch (NotEnoughTimeException | GamePhaseNotValidException e) {
            logger.log(e.getMessage());
        }

        return result;

    }

    private Move findBestMoveInCommunity(Set<Community> relevantCommunities, int depthLimit)
            throws OutOfTimeException {

        Move result = null;
        int score = Integer.MIN_VALUE;

        for (Community community : relevantCommunities) {

            logger.debug("Calculating best move for Community #" + community.hashCode());

            Game clonedGame = game.clone();
            clonedGame.communities.simulate(clonedGame.communities.get(community));

            Tuple<Move, Integer> communityResult =
                    findBestMove(clonedGame, sortMoves(game), depthLimit);

            logger.debug("Best Move is " + communityResult.first() + " with a score of " +
                    communityResult.second());

            if (communityResult.second() > score) {
                result = communityResult.first();
                score = communityResult.second();
            }

        }

        return result;
    }

    /**
     * Initialize the search, and thus begin the building of a search tree. If enough time, the
     * depth of the tree will be `depthLimit`.
     * @return The best move
     * @throws OutOfTimeException if we ran out of time
     */
    private Tuple<Move, Integer> findBestMove(Game game, List<Move> sortedMoves, int depth)
            throws OutOfTimeException {

        int resultScore = Integer.MIN_VALUE;
        Move resultMove = null;

        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        for (var move : sortedMoves) {

            Game clonedGame = game.clone();
            clonedGame.executeMove(move);
            int score = calculateScore(clonedGame, depth - 1, alpha, beta, true);

            if (score > resultScore) {
                resultScore = score;
                resultMove = move;
            }

            // Update alpha for the maximizer
            alpha = Math.max(alpha, score);
        }

        return new Tuple<>(resultMove, resultScore);
    }

    /**
     * Recursive function for calculating the score of a game situation.
     * @param depth Depth of tree that is built
     * @param alpha Lowest value that is allowed by Max
     * @param beta  Highest value that is allowed by Min
     * @return Best move with the belonging score
     */
    private int calculateScore(Game game, int depth, int alpha, int beta, boolean buildTree)
            throws OutOfTimeException {

        checkTime();

        List<Move> moves = evaluator.prepareMoves(game);

        if (depth == 0 || !game.getPhase().equals(GamePhase.BUILD) ||
                (game.communities != null && moves.stream().anyMatch(evaluator::isSpecialMove))) {

            if (game.getPhase() != GamePhase.BUILD) {
                bombPhasesReached++;
            }

            currentIterationNodesVisited++;
            return evaluator.evaluate(game, playerNumber);
        }

        boolean isMaximizer = game.getCurrentPlayerNumber() == playerNumber;

        if (isMaximizer) {

            int result = Integer.MIN_VALUE;

            for (Move move : moves) {

                Game clonedGame = game.clone();
                clonedGame.executeMove(move);
                int score = calculateScore(clonedGame, depth - 1, alpha, beta, true);

                result = Math.max(result, score);

                // Update alpha for the maximizer
                alpha = Math.max(alpha, score);

                // Beta cutoff
                if (beta <= alpha) {
                    evaluator.addCutoff(move, game.getMoveCounter());
                    break;
                }
            }

            return result;

        } else if (buildTree) {

            // Get Phi Move
            Move phi = moves.get(0);

            Game clonedGame = game.clone();
            clonedGame.executeMove(phi);
            int score = calculateScore(clonedGame, depth - 1, alpha, beta, true);

            int result = score;

            beta = Math.min(beta, result);

            // Minimizer -> Reverse
            Collections.reverse(moves);

            for (var move : moves) {

                clonedGame = game.clone();
                clonedGame.executeMove(move);
                score = calculateScore(clonedGame, depth - 1, alpha, beta, false);

                result = Math.min(result, score);

                // Update beta for the minimizer
                beta = Math.min(beta, result);

                // Alpha cutoff
                if (beta <= alpha) {
                    evaluator.addCutoff(move, game.getMoveCounter());
                    break;
                }
            }

            return result;

        } else {
            // TODO: Better heuristic? Maybe the move which gets us the most stones?
            Move move = moves.iterator().next();

            // TODO: Instead of cloning every layer, loop over one cloned game until maximizer?
            Game clonedGame = game.clone();
            clonedGame.executeMove(move);
            currentIterationNodesVisited++;

            return calculateScore(clonedGame, depth - 1, alpha, beta, false);
        }
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Utility
    |
    |-----------------------------------------------------------------------------------------------
    */

    /**
     * @param game The game.
     */
    private List<Move> sortMoves(Game game) throws OutOfTimeException {

        List<Tuple<Move, Integer>> data = new LinkedList<>();

        // Get data
        for (Move move : evaluator.prepareMoves(game)) {
            checkTime();

            Game clonedGame = game.clone();
            clonedGame.executeMove(move);

            data.add(new Tuple<>(move, evaluator.evaluate(clonedGame, playerNumber)));
        }

        checkTime();

        // Sort by evaluation score
        data.sort(Comparator.comparingInt(Tuple::second));

        // Reduce
        List<Move> result = new LinkedList<>();
        for (var tuple : data) {
            result.add(tuple.first());
        }

        return result;
    }

    /**
     * Checks if we are over the time limit
     * @throws OutOfTimeException if we ran out of time
     */
    private void checkTime() throws OutOfTimeException {
        if (System.currentTimeMillis() > endTime) {
            throw new OutOfTimeException("Out of time");
        }
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Stats
    |
    |-----------------------------------------------------------------------------------------------
    */

    /**
     * Stores the start timestamp of the latest iteration in the iterative deepening search.
     */
    private long currentIterationStartTime;

    /**
     * Stores the number of nodes visited (moves executed and games evaluated) of the current
     * iteration in the iterative deepening search.
     */
    private int currentIterationNodesVisited;

    private void resetStats() {
        currentIterationStartTime = System.currentTimeMillis();
        currentIterationNodesVisited = 1;
    }

    private void evaluateStats(int depth) throws NotEnoughTimeException {

        double totalTime = System.currentTimeMillis() - currentIterationStartTime;
        double timePerGame = totalTime / currentIterationNodesVisited;

        int branchingFactor =
                (int) Math.ceil(calculateBranchingFactor(currentIterationNodesVisited, depth));

        int newDepth = depth + 1;
        long timePassed = System.currentTimeMillis() - this.startTime;
        long timeLeft = this.endTime - System.currentTimeMillis();
        double timeEstimated = calculateNodeCountOfTree(branchingFactor, newDepth) * timePerGame;

        StringBuilder stats = new StringBuilder("Stats for depth " + depth + "\n");
        stats.append("Visited states: ").append(currentIterationNodesVisited).append("\n");
        stats.append("Total time: ").append(totalTime).append(" ms\n");
        stats.append("Time per state: ").append(timePerGame).append(" ms\n");
        stats.append("Average branching factor: ").append(branchingFactor).append("\n");
        /*stats.append("Cutoffs: ").append(moveCutoffs.values().stream()
                        .mapToInt(map -> map.values().stream().mapToInt(Integer::intValue).sum())
                        .sum())
                .append("\n");
        for (var cutoffs : moveCutoffs.entrySet()) {
            int count = cutoffs.getValue().values().stream().mapToInt(Integer::intValue).sum();
            stats.append("- ").append(count).append(" cutoffs on move ").append(cutoffs.getKey())
                    .append("\n");
        }*/
        logger.verbose(stats.toString());

        stats = new StringBuilder("Estimation for depth " + newDepth + "\n");
        stats.append("Time passed: ").append(timePassed).append(" ms\n");
        stats.append("Time left: ").append(timeLeft).append(" ms\n");
        stats.append("Time estimated: ").append(timeEstimated).append(" ms\n");
        logger.verbose(stats.toString());

        if (timeLeft < timeEstimated) {
            throw new NotEnoughTimeException(
                    "Estimated more time for the next depth than what's left");
        }

    }

    public static String getStats() {
        return "Timeouts: " + countElements(stats_timeouts) + "\nDepths searched: " +
                countElements(stats_depths);
    }

    public static <T> String countElements(List<T> list) {
        Map<T, Integer> stats = new HashMap<>();
        for (T item : list) {
            stats.put(item, stats.getOrDefault(item, 0) + 1);
        }
        StringBuilder result = new StringBuilder().append(list.size());
        for (Map.Entry<T, Integer> entry : stats.entrySet()) {
            result.append("\n- ").append(entry.getKey()).append(": ").append(entry.getValue());
        }
        return result.toString();
    }

}
