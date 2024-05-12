package clients;

import evaluation.GameEvaluator;
import exceptions.GamePhaseNotValidException;
import exceptions.OutOfTimeException;
import game.Game;
import game.GamePhase;
import move.Move;
import util.Logger;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class IterativeDeepeningAlphaBetaSearchClient extends Client {

    Logger logger = new Logger(this.getClass().getName());

    private final boolean moveSorting;
    private long startTime;
    private int timeLimit;

    public IterativeDeepeningAlphaBetaSearchClient(boolean moveSorting) {
        this.moveSorting = moveSorting;
        logger.log("Launching IterativeDeepeningAlphaBetaSearchClient");
    }

    @Override
    public Move sendMove(int timeLimit, int depthLimit) {
        this.startTime = System.currentTimeMillis();
        this.timeLimit = timeLimit - 100;
        if (game.getPhase() == GamePhase.END) {
            throw new GamePhaseNotValidException(
                    "Move was requested but we think the game already ended");
        }

        Move bestMove = null;
        try {
            for (int depth = 1; depth <= depthLimit; depth++) {
                initializeStats();
                if (System.currentTimeMillis() - startTime > this.timeLimit) {
                    throw new OutOfTimeException("Out of time");
                }
                logger.log("Calculating new move with time limit " + timeLimit +
                        "ms and depth limit " + depth + " layers");
                logger.debug("There are " + game.getValidMovesForCurrentPlayer().size() +
                        " possible moves, calculating the best scoring one\n");

                bestMove = alphaBetaSearch(depth);

                stats_depth = depth;
                logStats();
            }
        } catch (OutOfTimeException e) {
            Logger.get().log(e.getMessage());
        }


        assert bestMove != null;
        logger.log("Responding with " + bestMove.getClass().getSimpleName() +
                bestMove.getCoordinates());

        return bestMove;

    }

    private Move alphaBetaSearch(int depthLimit) {
        int resultScore = Integer.MIN_VALUE;
        Move resultMove = null;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        int i = 0;

        Set<Triplet<Game, Integer, Move>> nextGameScores = getFirstValidMoves();
        Set<Tuple<Game, Move>> gamesWithMoves;
        if (moveSorting) {
            gamesWithMoves = nextGameScores.stream()
                    .sorted(Comparator.comparing(Triplet::b, Comparator.reverseOrder()))
                    .map(t -> new Tuple<>(t.a, t.c))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        } else {
            gamesWithMoves = nextGameScores.stream().map(t -> new Tuple<>(t.a, t.c))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }

        for (Tuple<Game, Move> gamesWithMove : gamesWithMoves) {
            int score = minmaxWithDepth(gamesWithMove.a, depthLimit - 1, alpha, beta);
            logger.replace().debug("Move " + gamesWithMove.b + " has a score of " + score);
            if (score > resultScore) {
                resultScore = score;
                resultMove = gamesWithMove.b;
            }
            alpha = Math.max(alpha, score);  // Update alpha for the maximizer
            i++;
            int progressPercentage =
                    (int) ((float) i / (float) game.getValidMovesForCurrentPlayer().size() * 100);
            logger.debug(progressPercentage < 100 ? progressPercentage + "%" : "Done");

        }
        return resultMove;
    }

    private Set<Triplet<Game, Integer, Move>> getFirstValidMoves() {
        Set<Triplet<Game, Integer, Move>> nextGameScores = new LinkedHashSet<>();
        for (Move move : game.getValidMovesForCurrentPlayer()) {
            Game clonedGame = game.clone();
            clonedGame.executeMove(move);
            nextGameScores.add(
                    new Triplet<>(clonedGame, GameEvaluator.evaluate(clonedGame, ME), move));
        }
        return nextGameScores;
    }

    /**
     * @param depth Depth of tree that is built
     * @param alpha Lowest value that is allowed by Max
     * @param beta  Highest value that is allowed by Min
     * @return Best move with the belonging score
     */
    private int minmaxWithDepth(Game game, int depth, int alpha, int beta) {
        long stats_startTime;

        if (System.currentTimeMillis() - startTime > timeLimit) {
            throw new OutOfTimeException("Out of time");
        }

        if (depth == 0 || game.getPhase() != GamePhase.PHASE_1) {
            stats_gamesEvaluated++;
            stats_startTime = System.currentTimeMillis();

            int score = GameEvaluator.evaluate(game, ME);

            stats_evaluationTime += System.currentTimeMillis() - stats_startTime;

            return score;
        }
        int currentPlayerNumber = game.getCurrentPlayerNumber();
        boolean isMaximizer = currentPlayerNumber == ME;
        int result = isMaximizer ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (Move move : game.getValidMovesForCurrentPlayer()) {
            stats_gamesCalculated++;
            stats_startTime = System.currentTimeMillis();

            Game clonedGame = game.clone();

            stats_cloningTime += System.currentTimeMillis() - stats_startTime;
            stats_startTime = System.currentTimeMillis();

            clonedGame.executeMove(move);

            stats_executionTime += System.currentTimeMillis() - stats_startTime;

            int score = minmaxWithDepth(clonedGame, depth - 1, alpha, beta);
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
    private long stats_depth;


    private int stats_gamesCalculated;
    private long stats_cloningTime;
    private long stats_executionTime;

    private int stats_gamesEvaluated;
    private long stats_evaluationTime;

    private long stats_cutoffs;

    private void initializeStats() {
        stats_totalTime = System.currentTimeMillis();
        stats_gamesCalculated = 0;
        stats_cloningTime = 0;
        stats_executionTime = 0;
        stats_gamesEvaluated = 0;
        stats_evaluationTime = 0;
        stats_cutoffs = 0;
    }

    private void logStats() {

        logger.verbose("Actual depth: " + stats_depth);


        logger.verbose("Total time: " + (System.currentTimeMillis() - stats_totalTime));

        logger.verbose("Visited " + stats_gamesCalculated + " Games in " +
                (stats_cloningTime + stats_executionTime));
        logger.verbose("Cloning time: " + (stats_cloningTime));
        logger.verbose("Execution time: " + (stats_executionTime));

        logger.verbose("Cutoffs: " + stats_cutoffs);

        logger.verbose("Evaluated " + stats_gamesEvaluated + " Games in " + (stats_evaluationTime));

        logger.verbose("");

    }

    record Tuple<A, B>(
            A a,
            B b
    ) {

    }

    record Triplet<A, B, C>(
            A a,
            B b,
            C c
    ) {

    }

}
