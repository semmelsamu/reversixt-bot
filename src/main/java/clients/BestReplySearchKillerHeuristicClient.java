package clients;

import evaluation.GameEvaluator;
import exceptions.GamePhaseNotValidException;
import exceptions.OutOfTimeException;
import game.Game;
import game.GamePhase;
import move.Move;
import util.Logger;
import util.Triple;

import java.util.*;

public class BestReplySearchKillerHeuristicClient extends Client {

    Logger logger = new Logger(this.getClass().getName());

    private long startTime;
    private int timeLimit;

    private static final int TIME_BUFFER = 80;

    public BestReplySearchKillerHeuristicClient() {
        logger.log("Launching BestReplySearchKillerHeuristicClient");
    }

    @Override
    public Move sendMove(int timeLimit, int depthLimit) {

        this.timeLimit = timeLimit - TIME_BUFFER;

        // Fallback move
        Move bestMove = game.getValidMovesForCurrentPlayer().iterator().next(); // Any valid move

        try {

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

            evaluateStats(1);

            // Iterative deepening search
            // Start with depth 2 as depth 1 is already calculated via the sorted moves
            for (int depth = 2; timeLimit > 0 || depth <= depthLimit; depth++) {

                // For each depth we calculate the average time per game visited in order to
                // estimate a time for the next depth
                resetStats();

                bestMove = alphaBetaSearch(depth, sortedMoves);

                // Exit if we don't have the estimated time left
                evaluateStats(depth);
            }

            return bestMove;

        } catch (OutOfTimeException e) {
            logger.log(e.getMessage());
            return bestMove;
        }

    }

    private Move alphaBetaSearch(int depthLimit) throws OutOfTimeException {

        logger.log("Starting Alpha/Beta-Search with search depth " + depthLimit);

        int resultScore = Integer.MIN_VALUE;
        Move resultMove = null;

        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        stats_branchingFactors.add(game.getValidMovesForCurrentPlayer().size());

        // For logging progress percentage
        int i = 0;

        for (var moveAndGame : sortMoves(game, false)) {

            int progressPercentage =
                    (int) ((float) i / (float) game.getValidMovesForCurrentPlayer().size() * 100);
            logger.debug(progressPercentage + "%");

            int score = minmaxWithDepth(moveAndGame.second(), depthLimit - 1, alpha, beta);

            logger.replace().debug("Move " + moveAndGame.first() + " has first score of " + score);

            if (score > resultScore) {
                resultScore = score;
                resultMove = moveAndGame.first();
            }

            // Update alpha for the maximizer
            alpha = Math.max(alpha, score);

            i++;
        }

        return resultMove;
    }

    /**
     * @param depth Depth of tree that is built
     * @param alpha Lowest value that is allowed by Max
     * @param beta  Highest value that is allowed by Min
     * @return Best move with the belonging score
     */
    private int minmaxWithDepth(Game game, int depth, int alpha, int beta)
            throws OutOfTimeException {

        checkTime();

        if (depth == 0 || game.getPhase() != GamePhase.PHASE_1) {
            return GameEvaluator.evaluate(game, ME);
        }

        int currentPlayerNumber = game.getCurrentPlayerNumber();
        boolean isMaximizer = currentPlayerNumber == ME;

        int result = isMaximizer ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        stats_branchingFactors.add(game.getValidMovesForCurrentPlayer().size());

        for (Move move : game.getValidMovesForCurrentPlayer()) {

            Game clonedGame = game.clone();
            clonedGame.executeMove(move);
            stats_gamesVisited++;

            int score = minmaxWithDepth(clonedGame, depth - 1, alpha, beta);

            // Alpha-Beta-Pruning
            if (isMaximizer) {
                result = Math.max(result, score);
                alpha = Math.max(alpha, result);
                if (beta <= alpha) {
                    stats_cutoffs++;
                    break;  // Beta cutoff
                }
            } else {
                result = Math.min(result, score);
                beta = Math.min(beta, result);
                if (beta <= alpha) {
                    stats_cutoffs++;
                    break;  // Alpha cutoff
                }
            }
        }

        return result;
    }

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
            stats_gamesVisited++;

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
    private int stats_gamesVisited;
    private long stats_cutoffs;
    private List<Integer> stats_branchingFactors;

    private void resetStats() {
        stats_startTime = System.currentTimeMillis();
        stats_gamesVisited = 0;
        stats_cutoffs = 0;
        stats_branchingFactors = new LinkedList<>();
    }

    private void evaluateStats(int depth) throws OutOfTimeException {

        logger.verbose("- - - Stats for depth " + depth + " - - -");

        double totalTime = System.currentTimeMillis() - stats_startTime;
        logger.verbose("Total time: " + totalTime + " ms");

        logger.verbose("Visited Games: " + stats_gamesVisited);

        double timePerGame = totalTime / stats_gamesVisited;
        logger.verbose("Time per game: " + timePerGame + " ms");

        logger.verbose("Cutoffs: " + stats_cutoffs);

        double averageBranchingFactor =
                stats_branchingFactors.stream().mapToInt(Integer::intValue).average().orElse(0);
        logger.verbose("Average branching factor: " + averageBranchingFactor);

        int newDepth = depth + 1;

        logger.verbose("- - - Estimation for depth " + newDepth + " - - -");

        int timePassed = (int) (System.currentTimeMillis() - this.startTime);
        logger.verbose("Time passed: " + timePassed + " ms");

        int timeLeft = this.timeLimit - timePassed;
        logger.verbose("Time left: " + timeLeft + " ms");

        double timeEstimated = Math.pow(averageBranchingFactor, newDepth) * timePerGame;
        logger.verbose("Time estimated: " + timeEstimated + " ms");

        // Do not throw the OutOfTimeException if we don't have a time limit
        if (timeLimit == 0) {
            return;
        }

        if(timeLeft < timeEstimated)
            throw new OutOfTimeException("Estimated more time for the next depth than what's left");
    }

}
