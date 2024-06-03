package clients;

import evaluation.GameEvaluator;
import exceptions.GamePhaseNotValidException;
import exceptions.OutOfTimeException;
import game.Game;
import game.GamePhase;
import move.Move;
import network.Limit;
import util.Logger;
import util.Triple;
import util.Tuple;

import java.util.*;
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
    public Move sendMove(Limit type, int limit) {

        this.startTime = System.currentTimeMillis();
        this.timeLimit = timeLimit - TIME_BUFFER;

        if (game.getPhase() == GamePhase.END) {
            throw new GamePhaseNotValidException(
                    "Move was requested but we think the game already ended");
        }

        logger.log("Calculating new move with " + type + " limit " + limit);

        // Fallback if we can't calculate any depth
        Move bestMove = game.getValidMovesForCurrentPlayer().iterator().next(); // Any valid move

        try {
            for (int depth = 1; timeLimit > 0 || depth <= (type == Limit.DEPTH ? limit : 3);
                 depth++) {

                resetStats();

                bestMove = alphaBetaSearch(depth);

                // Only the first phase requires searching deeper than 1
                if (!game.getPhase().equals(GamePhase.PHASE_1)) {
                    break;
                }

                // Exit if we don't have the estimated time left
                if (evaluateStats(depth)) {
                    logger.log("Estimated more time for the next depth than what's left");
                    break;
                }
            }
        } catch (OutOfTimeException e) {
            logger.log(e.getMessage());
        }

        logger.log("Responding with " + bestMove);

        return bestMove;

    }

    private Move alphaBetaSearch(int depthLimit) throws OutOfTimeException {

        logger.log("Starting Alpha/Beta-Search with search depth " + depthLimit);

        int resultScore = Integer.MIN_VALUE;
        Move resultMove = null;

        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        stats_branchingFactors.add(game.getValidMovesForCurrentPlayer().size());

        Set<Triple<Game, Integer, Move>> nextGameScores = getGamesWithMoveAndEvaluation(game);
        Set<Tuple<Game, Move>> gamesWithMoves = new LinkedHashSet<>();

        if (enableMoveSorting) {
            List<Triple<Game, Integer, Move>> sortedList = new ArrayList<>(nextGameScores);
            sortedList.sort(Comparator.comparing(Triple::second, Comparator.reverseOrder()));

            for (Triple<Game, Integer, Move> t : nextGameScores) {
                gamesWithMoves.add(new Tuple<>(t.first(), t.third()));
            }

        } else {
            for (Triple<Game, Integer, Move> t : nextGameScores) {
                gamesWithMoves.add(new Tuple<>(t.first(), t.third()));
            }
        }

        // For logging progress percentage
        int i = 0;

        for (Tuple<Game, Move> gamesWithMove : gamesWithMoves) {

            int progressPercentage =
                    (int) ((float) i / (float) game.getValidMovesForCurrentPlayer().size() * 100);
            logger.debug(progressPercentage + "%");

            int score = minmaxWithDepth(gamesWithMove.first(), depthLimit - 1, alpha, beta);

            logger.replace().debug("Move " + gamesWithMove.second() + " has a score of " + score);

            if (score > resultScore) {
                resultScore = score;
                resultMove = gamesWithMove.second();
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

        if (System.currentTimeMillis() - startTime > timeLimit && timeLimit > 0) {
            throw new OutOfTimeException("Out of time");
        }

        if (depth == 0 || game.getPhase() != GamePhase.PHASE_1) {
            return GameEvaluator.evaluate(game, ME);
        }

        int currentPlayerNumber = game.getCurrentPlayerNumber();
        boolean isMaximizer = currentPlayerNumber == ME;

        int result = isMaximizer ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        stats_branchingFactors.add(game.getValidMovesForCurrentPlayer().size());

        if (enableMoveSorting && depth > 1) {

            Comparator<Integer> comparator =
                    isMaximizer ? Comparator.reverseOrder() : Comparator.naturalOrder();

            List<Triple<Game, Integer, Move>> sortedList =
                    new ArrayList<>(getGamesWithMoveAndEvaluation(game));
            sortedList.sort(Comparator.comparing(Triple::second, comparator));
            Set<Game> gamesWithMoves = new LinkedHashSet<>();
            for (Triple<Game, Integer, Move> t : sortedList) {
                gamesWithMoves.add(t.first());
            }

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
                } else {
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
        }

        return result;
    }

    private Set<Triple<Game, Integer, Move>> getGamesWithMoveAndEvaluation(Game game) {
        Set<Triple<Game, Integer, Move>> nextGameScores = new LinkedHashSet<>();
        for (Move move : game.getValidMovesForCurrentPlayer()) {
            Game clonedGame = game.clone();
            clonedGame.executeMove(move);
            stats_gamesVisited++;
            nextGameScores.add(
                    new Triple<>(clonedGame, GameEvaluator.evaluate(clonedGame, ME), move));
        }
        return nextGameScores;
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

    private boolean evaluateStats(int depth) {

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

        if (!(timeLimit > 0)) {
            return false;
        }

        return timeEstimated > timeLeft;
    }


}
