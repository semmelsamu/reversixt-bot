package clients;

import board.Direction;
import board.Tile;
import board.TileReader;
import evaluation.GameEvaluator;
import exceptions.GamePhaseNotValidException;
import exceptions.NotEnoughTimeException;
import exceptions.OutOfTimeException;
import game.Community;
import game.Game;
import game.GamePhase;
import move.Move;
import network.Limit;
import util.Logger;
import util.Quadruple;
import util.Tuple;

import java.util.*;

import static util.Tree.calculateBranchingFactor;
import static util.Tree.calculateNodeCountOfTree;

public class CommunitiesClient extends Client {

    Logger logger = new Logger(this.getClass().getName());

    /**
     * The time in milliseconds by which we want to respond earlier to avoid disqualification due to
     * network latency.
     */
    private static final int TIME_BUFFER = 1000;

    /**
     * The timestamp in milliseconds at which we got a move request.
     */
    private long startTime;

    /**
     * The very latest time by which we should send a move
     */
    private long endTime;

    /**
     * Used for statistics.
     */
    private int timeouts = 0;

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
     * States if we shall use a time or a depth limit for calculating a move.
     */
    private Limit type;

    private Community currentCommunity;

    /**
     * This is the entry point to the search for a new move.
     *
     * @param type  The type of the limit, either depth or time.
     * @param limit The amount of the limit. If depth limit, specifies the maximum depth the search
     *              tree shall be built, if time limit, specifies the maximum time the client has to
     *              respond with a move before it gets disqualified.
     * @return A valid move.
     */
    @Override
    public Move sendMove(Limit type, int limit) {

        this.type = type;
        this.startTime = System.currentTimeMillis();
        this.endTime = startTime + limit - TIME_BUFFER;

        // Fallback move
        Move bestMove =
                game.getRelevantMovesForCurrentPlayer().iterator().next(); // Random valid move

        try {

            // For each depth we calculate the average time per game visited in order to
            // estimate a time for the next depth
            resetStats();

            // Cache move sorting
            List<Tuple<Move, Game>> sortedMoves = sortMoves(game, new HashMap<>(), true);

            // As the sorted moves already contain the result for depth 1, update bestMove
            bestMove = sortedMoves.get(0).first();

            // It only makes sense to build a tree if we are in the first phase
            if (!game.getPhase().equals(GamePhase.BUILD)) {
                return bestMove;
            }

            moveCutoffs = new HashMap<>();

            // Exit if we don't have the estimated time left
            evaluateStats(1);

            bombPhasesReached = 0;

            // Iterative deepening search
            // Start with depth 2 as depth 1 is already calculated via the sorted moves
            List<Community> communities =
                    game.getGameStats().getCommunities().stream().filter(Community::isRelevant)
                            .toList();
            for (Community community : communities) {
                if (community.getAllKeys().contains(Tile.fromChar((char) (ME + '0')))) {
                    community.setRelevant(true);
                }
            }

            for (Community community : communities) {
                this.currentCommunity = community;
                for (int depthLimit = 2; type != Limit.DEPTH || depthLimit < limit; depthLimit++) {
                    resetStats();

                    bestMove = calculateBestMove(sortedMoves, depthLimit);

                    if (bombPhasesReached >= sortedMoves.size()) {
                        throw new GamePhaseNotValidException("Tree reached bomb phase");
                    }

                    evaluateStats(depthLimit);
                }
            }

            return bestMove;

        } catch (OutOfTimeException e) {
            timeouts++;
            logger.warn(e.getMessage());
            return bestMove;

        } catch (NotEnoughTimeException | GamePhaseNotValidException e) {
            logger.log(e.getMessage());
            return bestMove;
        }

    }

    /**
     * Initialize the search, and thus begin the building of a search tree. If enough time, the
     * depth of the tree will be `depthLimit`.
     *
     * @param sortedMoves The list of the initial sorted moves, consisting of a Tuple of the Move
     *                    itself and the Game after the move has been executed.
     * @return The best move
     * @throws OutOfTimeException if we ran out of time
     */
    private Move calculateBestMove(List<Tuple<Move, Game>> sortedMoves, int depth)
            throws OutOfTimeException {

        logger.log("Starting Alpha/Beta-Search with search depth " + depth);

        int resultScore = Integer.MIN_VALUE;
        Move resultMove = null;

        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        // For logging progress percentage
        int i = 0;

        logger.debug("0%");

        for (var moveAndGame : sortedMoves) {

            int score = calculateScore(moveAndGame.second(), depth - 1, alpha, beta, true);

            if (score > resultScore) {
                resultScore = score;
                resultMove = moveAndGame.first();
            }

            // Update alpha for the maximizer
            alpha = Math.max(alpha, score);

            // Log progress percentage
            i++;
            logger.replace().debug((int) ((float) i / (float) sortedMoves.size() * 100) + "%");
        }

        return resultMove;
    }

    /**
     * Recursive function for calculating the score of a game situation.
     *
     * @param depth Depth of tree that is built
     * @param alpha Lowest value that is allowed by Max
     * @param beta  Highest value that is allowed by Min
     * @return Best move with the belonging score
     */
    private int calculateScore(Game game, int depth, int alpha, int beta, boolean buildTree)
            throws OutOfTimeException {

        checkTime();

        if (depth == 0 || game.getPhase() != GamePhase.BUILD) {
            if (game.getPhase() != GamePhase.BUILD) {
                bombPhasesReached++;
            }
            currentIterationNodesVisited++;
            return GameEvaluator.evaluate(game, ME);
        }

        int currentPlayerNumber = game.getCurrentPlayerNumber();
        boolean isMaximizer = currentPlayerNumber == ME;

        if (isMaximizer) {
            int result = Integer.MIN_VALUE;

            List<Tuple<Move, Game>> sortedMoves = sortMoves(game,
                    moveCutoffs.getOrDefault(game.getMoveCounter(), new HashMap<>()), true);

            for (var moveAndGame : sortedMoves) {

                int score = calculateScore(moveAndGame.second(), depth - 1, alpha, beta, true);

                result = Math.max(result, score);

                // Update alpha for the maximizer
                alpha = Math.max(alpha, score);

                // Beta cutoff
                if (beta <= alpha) {
                    addCutoff(moveAndGame.first(), game.getMoveCounter());
                    break;
                }
            }

            return result;

        } else if (buildTree) {

            List<Tuple<Move, Game>> sortedMoves = sortMoves(game,
                    moveCutoffs.getOrDefault(game.getMoveCounter(), new HashMap<>()), false);

            // Get phi move
            // TODO: what if we have no moves?
            Tuple<Move, Game> phi = sortedMoves.get(0); // Best move for minimizer

            // Evaluate phi move
            int score = calculateScore(phi.second(), depth - 1, alpha, beta, true);
            int result = score;
            beta = Math.min(beta, result);

            for (var moveAndGame : sortedMoves) {
                score = calculateScore(moveAndGame.second(), depth - 1, alpha, beta, false);

                result = Math.min(result, score);

                // Update beta for the minimizer
                beta = Math.min(beta, result);

                // Alpha cutoff
                if (beta <= alpha) {
                    addCutoff(moveAndGame.first(), game.getMoveCounter());
                    break;
                }
            }

            return result;

        } else {
            // TODO: Better heuristic?
            //       maybe the move which gets us the most stones
            Move move = getMovesInCommunity(game).iterator().next(); // Random valid move

            // TODO: Instead of cloning every layer, loop over one cloned game until maximizer?
            Game clonedGame = game.clone();
            clonedGame.executeMove(move);
            nextPlayerInCommunity(clonedGame);

            currentIterationNodesVisited++;

            return calculateScore(clonedGame, depth - 1, alpha, beta, false);
        }
    }

    private Set<Move> getMovesInCommunity(Game game) {
        Set<Move> relevantMovesForCurrentPlayer = game.getRelevantMovesForCurrentPlayer();
        Set<Move> filteredMove = new HashSet<>();
        for (Move move : relevantMovesForCurrentPlayer) {
            for (Direction dir : Direction.values()) {
                TileReader reader = new TileReader(game, move.getCoordinates(), dir);
                if (reader.hasNext()) {
                    reader.next();
                    if (currentCommunity.getAllCoordinates().contains(reader.getCoordinates())) {
                        filteredMove.add(move);
                    }
                }
            }
        }
        return filteredMove;
    }

    private void nextPlayerInCommunity(Game clonedGame) {
        while (true) {
            Tile playerTile = Tile.fromChar((char) (clonedGame.getCurrentPlayerNumber() + '0'));
            if (!currentCommunity.getAllKeys().contains(playerTile)) {
                break;
            }
            clonedGame.nextPlayer();
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
     * Sort all possible moves the current player has by
     * 1. The number of cutoffs the same moves achieved in other branches on the same height of the
     * tree (killer heuristic)
     * 2. The score of the game after this move is executed.
     *
     * @param game        The initial game situation
     * @param moveCutoffs The killer heuristic
     */
    private List<Tuple<Move, Game>> sortMoves(Game game, Map<Move, Integer> moveCutoffs,
                                              boolean descending) throws OutOfTimeException {

        // A dataset where every entry consists of a Move, the Game after the Move execution, the
        // score of the game and the number of cutoffs this move achieved in other branches
        List<Quadruple<Move, Game, Integer, Integer>> result = new LinkedList<>();

        // Get data
        for (Move move : getMovesInCommunity(game)) {
            checkTime();

            Game clonedGame = game.clone();
            clonedGame.executeMove(move);
            nextPlayerInCommunity(clonedGame);
            int score = GameEvaluator.evaluate(clonedGame, ME);
            currentIterationNodesVisited++;

            int cutoffs = moveCutoffs.getOrDefault(move, 0);

            result.add(new Quadruple<>(move, clonedGame, score, cutoffs));
        }

        checkTime();

        // Sort. third = score, fourth = cutoffs
        result.sort((q1, q2) -> {
            int compareCutoffs = Integer.compare(q1.fourth(), q2.fourth());
            if (compareCutoffs != 0) {
                return compareCutoffs;
            } else {
                return Integer.compare(q1.third(), q2.third());
            }
        });

        checkTime();

        // Reduce to Tuple
        LinkedList<Tuple<Move, Game>> tuples = new LinkedList<>();
        for (var quadruple : result) {
            tuples.add(new Tuple<>(quadruple.first(), quadruple.second()));
        }

        // Reverse if necessary
        if (descending) {
            Collections.reverse(tuples);
        }

        return tuples;
    }

    /**
     * Checks if we are over the time limit
     *
     * @throws OutOfTimeException if we ran out of time
     */
    private void checkTime() throws OutOfTimeException {
        if (type == Limit.TIME && System.currentTimeMillis() > endTime) {
            throw new OutOfTimeException("Out of time");
        }
    }

    /**
     * Add a cutoff to the statistics.
     *
     * @param move  Which move achieved the cutoff
     * @param depth On which depth the cutoff was achieved
     */
    private void addCutoff(Move move, int depth) {
        moveCutoffs.putIfAbsent(depth, new HashMap<>());
        moveCutoffs.get(depth).put(move, moveCutoffs.get(depth).getOrDefault(move, 0) + 1);
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

        if (type == Limit.TIME && timeLeft < timeEstimated) {
            throw new NotEnoughTimeException(
                    "Estimated more time for the next depth than what's left");
        }

    }

    @Override
    public void exit() {
        logger.verbose("Total timeouts: " + timeouts);
    }

}
