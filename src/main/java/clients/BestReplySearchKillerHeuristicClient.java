package clients;

import evaluation.GameEvaluator;
import exceptions.GamePhaseNotValidException;
import exceptions.NotEnoughTimeException;
import exceptions.OutOfTimeException;
import game.Game;
import game.GamePhase;
import move.Move;
import util.Logger;
import util.Triple;

import java.util.*;

public class BestReplySearchKillerHeuristicClient extends Client {

    Logger logger = new Logger(this.getClass().getName());

    /**
     * The timestamp in milliseconds at which we got a move request.
     */
    private long startTime;

    /**
     * The time in milliseconds we have to calculate the current move.
     */
    private int timeLimit;

    /**
     * The time in milliseconds by which we want to respond earlier to avoid disqualification due to
     * network latency.
     */
    private static final int TIME_BUFFER = 80;

    /**
     * Used for statistics.
     */
    private int timeouts = 0;

    /**
     * Used for statistics.
     */
    private List<Integer> depths = new LinkedList<>();

    @Override
    public Move sendMove(int timeLimit, int depthLimit) {

        this.startTime = System.currentTimeMillis();
        this.timeLimit = timeLimit - TIME_BUFFER;

        // Fallback move
        Move bestMove = game.getValidMovesForCurrentPlayer().iterator().next(); // Random valid move

        int depth = 1;

        try {

            // For each depth we calculate the average time per game visited in order to
            // estimate a time for the next depth
            resetStats();

            if (game.getPhase() == GamePhase.END) {
                throw new GamePhaseNotValidException(
                        "Move was requested but we think the game already ended");
            }

            logger.log("Calculating new move with " +
                    (timeLimit > 0 ? "time limit " + timeLimit + " ms" :
                            "depth limit " + depthLimit + " layers"));


            // Cache move sorting
            List<Triple<Move, Game, Integer>> sortedMoves = sortMoves(game, true);

            // As the sorted moves already contain the result for depth 1, update bestMove
            bestMove = sortedMoves.get(0).first();

            // It only makes sense to build a tree if we are in the first phase
            if (!game.getPhase().equals(GamePhase.PHASE_1)) {
                return bestMove;
            }

            // Exit if we don't have the estimated time left
            evaluateStats(1);

            // Iterative deepening search
            // Start with depth 2 as depth 1 is already calculated via the sorted moves
            for (depth = 2; timeLimit > 0 || depth <= depthLimit; depth++) {
                resetStats();
                bestMove = initializeSearch(depth, sortedMoves);
                evaluateStats(depth);
            }

            if (game.getPhase().equals(GamePhase.PHASE_1)) {
                depths.add(depth);
            }

            return bestMove;

        } catch (OutOfTimeException e) {
            timeouts++;
            if (game.getPhase().equals(GamePhase.PHASE_1)) {
                depths.add(depth - 1);
            }
            logger.warn(e.getMessage());
            return bestMove;
        } catch (NotEnoughTimeException e) {
            logger.log(e.getMessage());
            if (game.getPhase().equals(GamePhase.PHASE_1)) {
                depths.add(depth);
            }
            return bestMove;
        }

    }

    private Move initializeSearch(int depthLimit, List<Triple<Move, Game, Integer>> sortedMoves)
            throws OutOfTimeException {

        logger.log("Starting Alpha/Beta-Search with search depth " + depthLimit);

        int resultScore = Integer.MIN_VALUE;
        Move resultMove = null;

        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        // For logging progress percentage
        int i = 0;

        logger.debug("0%");

        for (var moveAndGame : sortedMoves) {

            int score = search(moveAndGame.second(), depthLimit - 1, alpha, beta, true);

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
     * @param depth Depth of tree that is built
     * @param alpha Lowest value that is allowed by Max
     * @param beta  Highest value that is allowed by Min
     * @return Best move with the belonging score
     */
    private int search(Game game, int depth, int alpha, int beta, boolean buildTree)
            throws OutOfTimeException {

        checkTime();

        if (depth == 0 || game.getPhase() != GamePhase.PHASE_1) {
            stats_nodesVisited++;
            return GameEvaluator.evaluate(game, ME);
        }

        int currentPlayerNumber = game.getCurrentPlayerNumber();
        boolean isMaximizer = currentPlayerNumber == ME;

        if (isMaximizer) {
            int result = Integer.MIN_VALUE;

            List<Triple<Move, Game, Integer>> sortedMoves = sortMoves(game, true);

            for (var moveAndGame : sortedMoves) {

                int score = search(moveAndGame.second(), depth - 1, alpha, beta, true);

                result = Math.max(result, score);

                // Update alpha for the maximizer
                alpha = Math.max(alpha, score);

                // Beta cutoff
                if (beta <= alpha) {
                    stats_cutoffs++;
                    break;
                }
            }

            return result;
        } else if (buildTree) {
            int result = Integer.MAX_VALUE;

            Set<Move> moves = game.getValidMovesForCurrentPlayer();

            // TODO: Better heuristic?
            Move phi = moves.iterator().next(); // Random valid move

            for (Move move : game.getValidMovesForCurrentPlayer()) {
                Game clonedGame = game.clone();
                clonedGame.executeMove(move);
                stats_nodesVisited++;

                int score = search(clonedGame, depth - 1, alpha, beta, move.equals(phi));

                result = Math.min(result, score);

                // Update beta for the minimizer
                beta = Math.min(beta, result);

                // Alpha cutoff
                if (beta <= alpha) {
                    stats_cutoffs++;
                    break;
                }
            }

            return result;
        } else {
            // TODO: Better heuristic?
            Move move = game.getValidMovesForCurrentPlayer().iterator().next(); // Random valid move

            // TODO: Instead of cloning every layer, loop over one cloned game until maximizer?
            Game clonedGame = game.clone();
            clonedGame.executeMove(move);
            stats_nodesVisited++;

            return search(clonedGame, depth - 1, alpha, beta, false);
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
     * Execute all possible moves the current player has, evaluate the games after execution and
     * sort them by their evaluation score.
     */
    private List<Triple<Move, Game, Integer>> sortMoves(Game game, boolean descending)
            throws OutOfTimeException {
        Set<Triple<Move, Game, Integer>> result = new LinkedHashSet<>();

        // Get data
        for (Move move : game.getValidMovesForCurrentPlayer()) {
            checkTime();

            Game clonedGame = game.clone();
            clonedGame.executeMove(move);
            int score = GameEvaluator.evaluate(clonedGame, ME);
            stats_nodesVisited++;

            result.add(new Triple<>(move, clonedGame, score));
        }

        // Sort
        List<Triple<Move, Game, Integer>> list = new LinkedList<>(result);
        checkTime();
        list.sort(Comparator.comparingInt(Triple::third)); // Triple::third = score
        checkTime();
        if (descending) {
            Collections.reverse(list);
        }

        return list;
    }

    /**
     * Checks if we are over the time limit
     *
     * @throws OutOfTimeException if we ran out of time
     */
    private void checkTime() throws OutOfTimeException {
        if (System.currentTimeMillis() - startTime > timeLimit && timeLimit > 0) {
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

    private long stats_startTime;
    private int stats_nodesVisited;
    private long stats_cutoffs;

    private void resetStats() {
        stats_startTime = System.currentTimeMillis();
        stats_nodesVisited = 1;
        stats_cutoffs = 0;
    }

    private void evaluateStats(int depth) throws NotEnoughTimeException {

        double totalTime = System.currentTimeMillis() - stats_startTime;
        double timePerGame = totalTime / stats_nodesVisited;

        int branchingFactor = (int) Math.round(calculateBranchingFactor(stats_nodesVisited, depth));

        int newDepth = depth + 1;
        int timePassed = (int) (System.currentTimeMillis() - this.startTime);
        int timeLeft = this.timeLimit - timePassed;
        double timeEstimated = calculateNodeCountOfTree(branchingFactor, newDepth) * timePerGame;

        StringBuilder stats = new StringBuilder("Stats for depth " + depth + "\n");
        stats.append("Visited states: ").append(stats_nodesVisited).append("\n");
        stats.append("Total time: ").append(totalTime).append(" ms\n");
        stats.append("Time per state: ").append(timePerGame).append(" ms\n");
        stats.append("Cutoffs: ").append(stats_cutoffs).append("\n");
        stats.append("Average branching factor: ").append(branchingFactor);
        logger.verbose(stats.toString());

        stats = new StringBuilder("Estimation for depth " + newDepth + "\n");
        stats.append("Time passed: ").append(timePassed).append(" ms\n");
        stats.append("Time left: ").append(timeLeft).append(" ms\n");
        stats.append("Time estimated: ").append(timeEstimated).append(" ms\n");
        logger.verbose(stats.toString());

        // Do not throw the OutOfTimeException if we don't have a time limit
        if (timeLimit == 0) {
            return;
        }

        if (timeLeft < timeEstimated) {
            throw new NotEnoughTimeException(
                    "Estimated more time for the next depth than what's left");
        }

    }

    public static double calculateBranchingFactor(int n, int d) {
        double min = 1.0;
        double max = 10.0;
        double tolerance = 1e-10;
        double mid = 0;

        while ((max - min) > tolerance) {
            mid = (min + max) / 2;
            double result = Math.pow(mid, d + 1) - n * mid + (n - 1);

            if (result == 0.0) {
                break;
            } else if (result < 0) {
                min = mid;
            } else {
                max = mid;
            }
        }

        return mid;
    }

    /**
     * Calculate the number of nodes a t-ary tree with depth d has.
     */
    private static int calculateNodeCountOfTree(int t, int d) {
        int result = 0;
        for (int i = 0; i <= d; i++) {
            result += Math.pow(t, i);
        }
        return result;
    }

    public void end() {
        logger.verbose("End statistics\nTimeouts: " + timeouts + ((timeLimit > 0) ?
                "\nAverage depth:" + depths.stream().mapToInt(Integer::intValue).average().orElse(0) : ""));
    }

}
