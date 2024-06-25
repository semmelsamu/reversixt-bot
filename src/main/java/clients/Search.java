package clients;

import evaluation.GameEvaluator;
import exceptions.GamePhaseNotValidException;
import exceptions.NotEnoughTimeException;
import exceptions.OutOfTimeException;
import game.Game;
import game.GamePhase;
import move.Move;
import stats.Community;
import util.Logger;
import util.Quadruple;
import util.Triple;
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
     * Stores how many cutoffs a move on a certain depth has achieved.
     */
    private Map<Integer, Map<Move, Integer>> moveCutoffs;

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
        Tuple<Move, Integer> result =
                new Tuple<>(game.getRelevantMovesForCurrentPlayer().iterator().next(),
                        Integer.MIN_VALUE);

        try {

            stats_depths.add(0);

            // For each depth we calculate the average time per game visited in order to
            // estimate a time for the next depth
            resetStats();

            // It only makes sense to build a tree if we are in the first phase
            if (!game.getPhase().equals(GamePhase.BUILD)) {
                throw new GamePhaseNotValidException("Not in build phase, so no tree building");
            }

            moveCutoffs = new HashMap<>();
            bombPhasesReached = 0;

            // Iterative deepening search
            int depthLimit = 1;

            while (true) {

                logger.log("Iterative deepening: Depth " + depthLimit);

                resetStats();

                Set<Tuple<Game, Community>> relevantCommunities =
                        game.communities.getRelevantCommunities(playerNumber);

                logger.debug(relevantCommunities.size() + " relevant Communities");

                for (var gameAndCommunity : relevantCommunities) {

                    logger.debug("Calculating best move for Community #" +
                            gameAndCommunity.second().hashCode());

                    var communityResult = findBestMove(
                            fullMoveSort(gameAndCommunity.first(), gameAndCommunity.second()),
                            depthLimit);

                    logger.debug("Best Move is " + communityResult.first() + " with a score of " +
                            communityResult.second());

                    if (communityResult.second() > result.second()) {
                        result = communityResult;
                    }

                }

                stats_depths.add(depthLimit);

                if (bombPhasesReached >= game.getValidMovesForCurrentPlayer().size()) {
                    throw new GamePhaseNotValidException("Tree reached bomb phase");
                }

                evaluateStats(depthLimit);

                depthLimit++;
            }

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

        // Result = Move and Score. Return only the Move.
        return result.first();

    }

    /**
     * Initialize the search, and thus begin the building of a search tree. If enough time, the
     * depth of the tree will be `depthLimit`.
     * @return The best move
     * @throws OutOfTimeException if we ran out of time
     */
    private Tuple<Move, Integer> findBestMove(
            List<Triple<Move, Game, Community>> fullMoveSortResult, int depth)
            throws OutOfTimeException {

        int resultScore = Integer.MIN_VALUE;
        Move resultMove = null;

        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        for (var moveGameCommunityTriple : fullMoveSortResult) {

            int score = calculateScore(moveGameCommunityTriple.second(),
                    moveGameCommunityTriple.third(), depth - 1, alpha, beta, true);

            if (score > resultScore) {
                resultScore = score;
                resultMove = moveGameCommunityTriple.first();
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
    private int calculateScore(Game game, Community community, int depth, int alpha, int beta,
                               boolean buildTree) throws OutOfTimeException {

        checkTime();

        if (depth == 0 || !game.getPhase().equals(GamePhase.BUILD) ||
                !community.anyPlayerHasValidMoves()) {
            if (game.getPhase() != GamePhase.BUILD) {
                bombPhasesReached++;
            }
            currentIterationNodesVisited++;
            return evaluator.evaluate(game, playerNumber);
        }

        boolean isMaximizer = game.getCurrentPlayerNumber() == playerNumber;

        if (isMaximizer) {

            int result = Integer.MIN_VALUE;

            for (Move move : community.getRelevantMovesForCurrentPlayer()) {

                Tuple<Game, Community> executionResult = executeMove(game, move);

                int score =
                        calculateScore(executionResult.first(), executionResult.second(), depth - 1,
                                alpha, beta, true);

                result = Math.max(result, score);

                // Update alpha for the maximizer
                alpha = Math.max(alpha, score);

                // Beta cutoff
                if (beta <= alpha) {
                    addCutoff(move, game.getMoveCounter());
                    break;
                }
            }

            return result;

        } else if (buildTree) {

            // Phi move
            List<Move> moves = new ArrayList<>(community.getRelevantMovesForCurrentPlayer());
            moves.sort(evaluator);
            Move phi = moves.get(0);
            Tuple<Game, Community> executionResult = executeMove(game, phi);

            int score = calculateScore(executionResult.first(), executionResult.second(), depth - 1,
                    alpha, beta, true);

            int result = score;

            beta = Math.min(beta, result);

            for (var move : community.getRelevantMovesForCurrentPlayer()) {
                executionResult = executeMove(game, phi);

                score = calculateScore(executionResult.first(), executionResult.second(), depth - 1,
                        alpha, beta, false);

                result = Math.min(result, score);

                // Update beta for the minimizer
                beta = Math.min(beta, result);

                // Alpha cutoff
                if (beta <= alpha) {
                    addCutoff(move, game.getMoveCounter());
                    break;
                }
            }

            return result;

        } else {
            // TODO: Better heuristic? Maybe the move which gets us the most stones?
            Move move = community.getRelevantMovesForCurrentPlayer().iterator().next();

            // TODO: Instead of cloning every layer, loop over one cloned game until maximizer?
            Tuple<Game, Community> executionResult = executeMove(game, move);
            currentIterationNodesVisited++;

            return calculateScore(executionResult.first(), executionResult.second(), depth - 1,
                    alpha, beta, false);
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
     * Sort all Moves the next valid Player in a given Community has by executing them and fully
     * evaluating the resulted Game.
     * @param game      The game.
     * @param community The community which contains the moves that should be sorted.
     * @return A sorted LinkedList with Triples, where each Triple contains:
     * <ol>
     *     <li>The Move</li>
     *     <li>The Game after executing the Move</li>
     *     <li>The Move's target Community after the Move execution, or null if the Game reached
     *     the bomb phase</li>
     * </ol>
     */
    private List<Triple<Move, Game, Community>> fullMoveSort(Game game, Community community)
            throws OutOfTimeException {

        List<Quadruple<Move, Game, Community, Integer>> result = new LinkedList<>();

        // Get data
        for (Move move : community.getRelevantMovesForCurrentPlayer()) {
            checkTime();

            Tuple<Game, Community> executionResult = executeMove(game, move);
            currentIterationNodesVisited++;

            result.add(new Quadruple<>(move, executionResult.first(), executionResult.second(),
                    evaluator.evaluate(game, playerNumber)));
        }

        checkTime();

        // Sort by evaluation score
        result.sort(Comparator.comparingInt(Quadruple::fourth));

        // Reduce to Triple
        LinkedList<Triple<Move, Game, Community>> triples = new LinkedList<>();
        for (var quadruple : result) {
            triples.add(new Triple<>(quadruple.first(), quadruple.second(), quadruple.third()));
        }

        return triples;
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

    /**
     * Add a cutoff to the statistics.
     * @param move  Which move achieved the cutoff
     * @param depth On which depth the cutoff was achieved
     */
    private void addCutoff(Move move, int depth) {
        moveCutoffs.putIfAbsent(depth, new HashMap<>());
        moveCutoffs.get(depth).put(move, moveCutoffs.get(depth).getOrDefault(move, 0) + 1);
    }

    /**
     * Clone the Game, execute the Move and find the new Community.
     * @return A Tuple consisting of the new Game and the new Community.
     */
    private static Tuple<Game, Community> executeMove(Game game, Move move) {
        Game clonedGame = game.clone();
        clonedGame.executeMove(move);

        Community clonedCommunity = null;

        if (clonedGame.communities != null) {
            clonedCommunity =
                    clonedGame.communities.findCommunityByCoordinates(move.getCoordinates());
            if (clonedCommunity.getRelevantMovesForCurrentPlayer().isEmpty() &&
                    clonedCommunity.anyPlayerHasValidMoves()) {
                clonedCommunity.nextPlayer();
            }
        }

        return new Tuple<>(clonedGame, clonedCommunity);
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
        stats.append("Cutoffs: ").append(moveCutoffs.values().stream()
                        .mapToInt(map -> map.values().stream().mapToInt(Integer::intValue).sum()).sum())
                .append("\n");
        for (var cutoffs : moveCutoffs.entrySet()) {
            int count = cutoffs.getValue().values().stream().mapToInt(Integer::intValue).sum();
            stats.append("- ").append(count).append(" cutoffs on move ").append(cutoffs.getKey())
                    .append("\n");
        }
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
