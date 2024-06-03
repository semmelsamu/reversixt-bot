package clients;

import evaluation.GameEvaluator;
import exceptions.GamePhaseNotValidException;
import game.Game;
import game.GamePhase;
import move.Move;
import network.Limit;
import util.Logger;

public class OptimizedParanoidClient extends Client {

    Logger logger = new Logger(this.getClass().getName());

    public OptimizedParanoidClient() {
        logger.log("Launching Optimized Paranoid Client");
    }

    @Override
    public Move sendMove(Limit type, int limit) {

        if (game.getPhase() == GamePhase.END) {
            throw new GamePhaseNotValidException(
                    "Move was requested but we think the game already ended");
        }

        initializeStats();

        logger.log("Calculating new move with " + type + " limit " + limit);

        logger.debug("There are " + game.getValidMovesForCurrentPlayer().size() +
                " possible moves, calculating the best scoring one\n");

        int resultScore = Integer.MIN_VALUE;
        Move resultMove = null;

        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        int i = 0;
        for (Move move : game.getValidMovesForCurrentPlayer()) {

            Game clonedGame = game.clone();
            clonedGame.executeMove(move);
            int score = minmax(clonedGame, type == Limit.DEPTH ? limit - 1 : 3, alpha, beta);

            logger.replace().debug("Move " + move + " has a score of " + score);

            if (score > resultScore) {
                resultScore = score;
                resultMove = move;
            }

            alpha = Math.max(alpha, score);  // Update alpha for the maximizer

            i++;
            int progressPercentage = (int) ((float) i / (float) game.getValidMovesForCurrentPlayer().size() * 100);
            logger.debug(progressPercentage < 100 ? progressPercentage + "%" : "Done");
        }

        logStats();

        logger.log("Responding with " + resultMove.getClass().getSimpleName() +
                resultMove.getCoordinates() + " which has a score of " + resultScore);

        return resultMove;

    }

    /**
     * @param depth Depth of tree that is built
     * @param alpha Lowest value that is allowed by Max
     * @param beta  Highest value that is allowed by Min
     * @return Best move with the belonging score
     */
    private int minmax(Game game, int depth, int alpha, int beta) {
        long stats_startTime;

        if (depth == 0 || game.getPhase() != GamePhase.PHASE_1) {
            stats_gamesEvaluated++;
            stats_startTime = System.nanoTime();
            int score = GameEvaluator.evaluate(game, ME);
            stats_evaluationTime += System.nanoTime() - stats_startTime;
            return score;
        }

        int currentPlayerNumber = game.getCurrentPlayerNumber();

        boolean isMaximizer = currentPlayerNumber == ME;

        int result = isMaximizer ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (Move move : game.getValidMovesForCurrentPlayer()) {

            stats_gamesCalculated++;

            stats_startTime = System.nanoTime();
            Game clonedGame = game.clone();
            stats_cloningTime += System.nanoTime() - stats_startTime;

            stats_startTime = System.nanoTime();
            clonedGame.executeMove(move);
            stats_executionTime += System.nanoTime() - stats_startTime;

            int score = minmax(clonedGame, depth - 1, alpha, beta);

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

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Stats
    |
    |-----------------------------------------------------------------------------------------------
    */

    private long stats_totalTime;

    private int stats_gamesCalculated;
    private long stats_cloningTime;
    private long stats_executionTime;

    private int stats_gamesEvaluated;
    private long stats_evaluationTime;

    private long stats_cutoffs;

    private String ms(long nanoseconds) {
        return nanoseconds / 1_000_000 + "ms";
    }

    private void initializeStats() {
        stats_totalTime = System.nanoTime();
        stats_gamesCalculated = 0;
        stats_cloningTime = 0;
        stats_executionTime = 0;
        stats_gamesEvaluated = 0;
        stats_evaluationTime = 0;
        stats_cutoffs = 0;
    }

    private void logStats() {

        logger.verbose("Total time: " + ms(System.nanoTime() - stats_totalTime));

        logger.verbose("Visited " + stats_gamesCalculated + " Games in " +
                ms(stats_cloningTime + stats_executionTime));
        logger.verbose("Cloning time: " + ms(stats_cloningTime));
        logger.verbose("Execution time: " + ms(stats_executionTime));

        logger.verbose("Cutoffs: " + stats_cutoffs);

        logger.verbose(
                "Evaluated " + stats_gamesEvaluated + " Games in " + ms(stats_evaluationTime));
    }

}
