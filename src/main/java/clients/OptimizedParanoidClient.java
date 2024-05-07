package clients;

import evaluation.GameEvaluator;
import game.Game;
import game.GamePhase;
import game.MoveCalculator;
import game.MoveExecutor;
import move.Move;
import util.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OptimizedParanoidClient extends Client {

    Logger logger = new Logger(this.getClass().getName());

    public OptimizedParanoidClient() {
        logger.log("Launching Optimized Paranoid Client with depth limit");
    }

    @Override
    public Move sendMove(int timeLimit, int depthLimit) {

        if (game.getPhase() == GamePhase.END) {
            logger.error("Move was requested but we think the game already ended");
            return null;
        }

        initializeStats();

        logger.log("Calculating new move (time/depth) " + timeLimit + " " + depthLimit);

        Map.Entry<Move, Integer> result =
                minmax(game, depthLimit, Integer.MIN_VALUE, Integer.MAX_VALUE);

        logger.log("Done");

        logStats();

        logger.log("Responding with " + result.getKey().getClass().getSimpleName() +
                result.getKey().getCoordinates() + " which has a score of " + result.getValue());

        return result.getKey();

    }

    /**
     * @param depth Depth of tree that is built
     * @param alpha Lowest value that is allowed by Max
     * @param beta  Highest value that is allowed by Min
     * @return Best move with the belonging score
     */
    private Map.Entry<Move, Integer> minmax(Game game, int depth, int alpha, int beta) {

        long startTime; // For logging stats

        depth--;

        boolean max = ME == game.getCurrentPlayerNumber();

        Move resultMove = null;
        int resultScore = max ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        startTime = System.nanoTime(); // For logging stats
        Set<Move> possibleMoves =
                MoveCalculator.getValidMovesForPlayer(game, game.getCurrentPlayerNumber());
        stats_calculationTime += (System.nanoTime() - startTime); // For logging stats

        stats_branchingFactors.add(possibleMoves.size()); // For logging stats

        for (Move move : possibleMoves) {

            startTime = System.nanoTime(); // For logging stats
            Game clonedGame = game.clone();
            stats_cloningTime += (System.nanoTime() - startTime); // For logging stats

            startTime = System.nanoTime(); // For logging stats
            MoveExecutor.executeMove(clonedGame, move);
            stats_executionTime += (System.nanoTime() - startTime); // For logging stats

            stats_gamesVisited++; // For logging stats

            int score;
            if (depth > 0 && clonedGame.getPhase() == GamePhase.PHASE_1) {
                score = minmax(clonedGame, depth, alpha, beta).getValue();
            } else {
                startTime = System.nanoTime(); // For logging stats
                score = GameEvaluator.evaluate(game, ME);
                stats_evaluationTime += (System.nanoTime() - startTime); // For logging stats
                stats_gamesEvaluated++; // For logging stats
            }

            if (max && (score > resultScore)) {
                resultScore = score;
                resultMove = move;
                alpha = Math.max(alpha, resultScore);
                // Max-Skip
                if (resultScore >= beta) {
                    stats_maxSkips++; // For logging stats
                    break;
                }
            }
            if (!max && (score < resultScore)) {
                resultScore = score;
                resultMove = move;
                beta = Math.min(beta, resultScore);
                // Min-Skip
                if (resultScore <= alpha) {
                    stats_minSkips++; // For logging stats
                    break;
                }
            }
        }

        // This should never happen because if we have no moves
        // the game would be evaluated instantly
        assert resultMove != null;

        return Map.entry(resultMove, resultScore);
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Stats
    |
    |-----------------------------------------------------------------------------------------------
    */

    private long stats_totalTime;

    private int stats_gamesVisited;
    private int stats_gamesEvaluated;

    private int stats_minSkips;
    private int stats_maxSkips;

    private long stats_evaluationTime;
    private long stats_cloningTime;
    private long stats_calculationTime;
    private long stats_executionTime;

    private List<Integer> stats_branchingFactors;

    private void initializeStats() {
        stats_branchingFactors = new LinkedList<>();
        stats_gamesVisited = 0;
        stats_gamesEvaluated = 0;
        stats_minSkips = 0;
        stats_maxSkips = 0;
        stats_evaluationTime = 0;
        stats_cloningTime = 0;
        stats_calculationTime = 0;
        stats_executionTime = 0;
        stats_totalTime = System.nanoTime();
    }

    private void logStats() {

        logger.verbose("Stats:");

        logger.verbose("Total time: " + (System.nanoTime() - stats_totalTime) / 1_000_000 + "ms");

        logger.verbose("Possible moves: " + stats_branchingFactors.get(0));

        logger.verbose("Games visited: " + stats_gamesVisited);
        logger.verbose("Total cloning time: " + stats_cloningTime / 1_000_000 + "ms");
        logger.verbose("Average cloning time per game: " +
                (float) stats_cloningTime / (float) stats_gamesVisited / 1_000_000 + "ms");

        logger.verbose("Total move calculation time: " + stats_calculationTime / 1_000_000 + "ms");
        logger.verbose("Average calculation time per move: " +
                (float) stats_calculationTime / (float) stats_gamesVisited / 1_000_000 + "ms");

        logger.verbose("Total move execution time: " + stats_executionTime / 1_000_000 + "ms");
        logger.verbose("Average execution time per move: " +
                (float) stats_executionTime / (float) stats_gamesVisited / 1_000_000 + "ms");

        logger.verbose("Average branching factor: " +
                stats_branchingFactors.stream().mapToInt(Integer::intValue).average().orElse(0.0));

        logger.verbose(
                "Min-Skips: " + stats_minSkips + ", Max-Skips: " + stats_maxSkips + ", Total: " +
                        (stats_minSkips + stats_maxSkips));

        logger.verbose("Games evaluated: " + stats_gamesEvaluated);
        logger.verbose("Total evaluation time: " + stats_evaluationTime / 1_000_000 + "ms");
        logger.verbose("Average evaluation time per game: " +
                (float) stats_evaluationTime / (float) stats_gamesEvaluated / 1_000_000 + "ms");

    }

}
