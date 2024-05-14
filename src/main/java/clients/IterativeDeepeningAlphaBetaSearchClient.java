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

    private final boolean enableMoveSorting;
    private long startTime;
    private int timeLimit;

    private static final int TIME_BUFFER = 80;

    public IterativeDeepeningAlphaBetaSearchClient(boolean enableMoveSorting) {
        this.enableMoveSorting = enableMoveSorting;
        logger.log("Launching IterativeDeepeningAlphaBetaSearchClient with move sorting " +
                (enableMoveSorting ? "enabled" : "disabled"));
    }

    @Override
    public Move sendMove(int timeLimit, int depthLimit) {

        this.startTime = System.currentTimeMillis();
        this.timeLimit = timeLimit - TIME_BUFFER;

        if (game.getPhase() == GamePhase.END) {
            throw new GamePhaseNotValidException(
                    "Move was requested but we think the game already ended");
        }

        logger.log("Calculating new move with " +
                (timeLimit > 0 ? "time limit " + timeLimit + "ms" :
                        "depth limit " + depthLimit + " layers"));

        // Fallback if we can't calculate any depth
        Move bestMove = game.getValidMovesForCurrentPlayer().iterator().next(); // Any valid move

        try {
            for (int depth = 1; timeLimit > 0 || depth <= depthLimit; depth++) {

                initializeStats();

                bestMove = alphaBetaSearch(depth);

                logger.verbose("Stats for depth " + depth + ":");
                logStats();
            }
        } catch (OutOfTimeException e) {
            logger.log(e.getMessage());
        }

        logger.log("Responding with " + bestMove);

        return bestMove;

    }

    private Move alphaBetaSearch(int depthLimit) throws OutOfTimeException {

        int resultScore = Integer.MIN_VALUE;
        Move resultMove = null;

        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        Set<Triplet<Game, Integer, Move>> nextGameScores = getGamesWithMoveAndEvaluation(game);
        Set<Tuple<Game, Move>> gamesWithMoves;

        if (enableMoveSorting) {
            // TODO: Performance -> No Streams!
            gamesWithMoves = nextGameScores.stream()
                    .sorted(Comparator.comparing(Triplet::b, Comparator.reverseOrder()))
                    .map(t -> new Tuple<>(t.a, t.c))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        } else {
            // TODO: Performance -> No Streams!
            gamesWithMoves = nextGameScores.stream().map(t -> new Tuple<>(t.a, t.c))
                    .collect(Collectors.toSet());
        }

        stats_gamesVisited += gamesWithMoves.size();

        // For logging progress percentage
        int i = 0;
        logger.log("Starting Alpha-Beta-Search with depth limit " + depthLimit);

        for (Tuple<Game, Move> gamesWithMove : gamesWithMoves) {

            int score = minmaxWithDepth(gamesWithMove.a, depthLimit - 1, alpha, beta);

            logger.replace().debug("Move " + gamesWithMove.b + " has a score of " + score);

            if (score > resultScore) {
                resultScore = score;
                resultMove = gamesWithMove.b;
            }

            // Update alpha for the maximizer
            alpha = Math.max(alpha, score);


            // Logging progress percentage
            i++;
            int progressPercentage =
                    (int) ((float) i / (float) game.getValidMovesForCurrentPlayer().size() * 100);
            logger.debug(progressPercentage < 100 ? progressPercentage + "%" : "Done");
        }

        return resultMove;
    }

    private Set<Triplet<Game, Integer, Move>> getGamesWithMoveAndEvaluation(Game game) {
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
    private int minmaxWithDepth(Game game, int depth, int alpha, int beta)
            throws OutOfTimeException {

        if (System.currentTimeMillis() - startTime > timeLimit) {
            throw new OutOfTimeException("Out of time");
        }

        if (depth == 0 || game.getPhase() != GamePhase.PHASE_1) {
            return GameEvaluator.evaluate(game, ME);
        }

        int currentPlayerNumber = game.getCurrentPlayerNumber();
        boolean isMaximizer = currentPlayerNumber == ME;

        int result = isMaximizer ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        if (enableMoveSorting && depth > 1) {

            Comparator<Integer> comparator =
                    isMaximizer ? Comparator.reverseOrder() : Comparator.naturalOrder();

            // TODO: Performance -> No Streams!
            Set<Game> gamesWithMoves = getGamesWithMoveAndEvaluation(game).stream()
                    .sorted(Comparator.comparing(Triplet::b, comparator)).map(Triplet::a)
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            stats_gamesVisited += gamesWithMoves.size();

            for (Game clonedGame : gamesWithMoves) {

                int score = minmaxWithDepth(clonedGame, depth - 1, alpha, beta);

                // Alpha-Beta-Pruning
                if (isMaximizer) {
                    result = Math.max(result, score);
                    alpha = Math.max(alpha, result);
                    if (beta <= alpha) {
                        stats_cutoffs++;
                        break;  // Beta cutoff
                    }
                }
                else {
                    result = Math.min(result, score);
                    beta = Math.min(beta, result);
                    if (beta <= alpha) {
                        stats_cutoffs++;
                        break;  // Alpha cutoff
                    }
                }
            }
        } else {
            for (Move move : game.getValidMovesForCurrentPlayer()) {

                stats_gamesVisited++;

                Game clonedGame = game.clone();
                clonedGame.executeMove(move);

                int score = minmaxWithDepth(clonedGame, depth - 1, alpha, beta);

                // Alpha-Beta-Pruning
                if (isMaximizer) {
                    result = Math.max(result, score);
                    alpha = Math.max(alpha, result);
                    if (beta <= alpha) {
                        stats_cutoffs++;
                        break;  // Beta cutoff
                    }
                }
                else {
                    result = Math.min(result, score);
                    beta = Math.min(beta, result);
                    if (beta <= alpha) {
                        stats_cutoffs++;
                        break;  // Alpha cutoff
                    }
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


    private int stats_gamesVisited;

    private long stats_cutoffs;

    private void initializeStats() {
        stats_totalTime = System.currentTimeMillis();
        stats_gamesVisited = 0;
        stats_cutoffs = 0;
    }

    private void logStats() {
        logger.verbose("Total time: " + (System.currentTimeMillis() - stats_totalTime) + " ms");
        logger.verbose("Visited Games: " + stats_gamesVisited);
        logger.verbose("Cutoffs: " + stats_cutoffs);
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
