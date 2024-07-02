package clients;

import evaluation.GameEvaluator;
import exceptions.GamePhaseNotValidException;
import exceptions.NotEnoughTimeException;
import exceptions.OutOfTimeException;
import game.Game;
import game.GamePhase;
import move.Move;
import util.Logger;
import util.Timer;
import util.Tuple;

import java.util.Collections;
import java.util.List;

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
     * The timer used to determine how much time is left and check for timeouts
     */
    private final SearchTimer timer;

    /**
     * Initialize a new move search.
     * @param game         The game for which to search the best move.
     * @param playerNumber The player for which to search the best move.
     */
    public Search(Game game, Timer timer, int playerNumber, GameEvaluator evaluator) {
        this.game = game;
        this.timer = new SearchTimer(timer);
        this.playerNumber = playerNumber;
        this.evaluator = evaluator;
    }

    /**
     * Start the search.
     * @return A valid move.
     */
    public Move search(int depth) {

        // Fallback - Random move
        Move result = game.getValidMoves().iterator().next();

        try {

            // Fast approximation
            List<Move> sortedMoves = evaluator.sortMovesQuick(game);
            result = sortedMoves.get(sortedMoves.size() - 1);

            timer.checkTime();

            // Better approximation
            List<Tuple<Move, Game>> sortedMovesAndGame = evaluator.sortMoves(game, timer);
            // Max-Player -> Reverse
            Collections.reverse(sortedMovesAndGame);
            result = sortedMovesAndGame.get(0).first();

            SearchStats.incrementDepthsSearched(0);

            timer.checkTime();
            timer.checkFirstDepth(sortedMovesAndGame.size());

            // Iterative deepening search
            int depthLimit = 2;
            do {
                logger.log("Iterative deepening: Depth " + depthLimit);

                timer.reset();

                // Perform actual search
                result = findBestMove(game, sortedMovesAndGame, depthLimit);

                SearchStats.incrementDepthsSearched(depthLimit);

                timer.checkAbort(depthLimit);

                depthLimit++;

            } while (game.getPhase().equals(GamePhase.BUILD) && depthLimit < depth);

            logger.log("Exiting iterative deepening");

        }
        catch (OutOfTimeException e) {
            logger.warn(e.getMessage());
        }
        catch (NotEnoughTimeException | GamePhaseNotValidException e) {
            logger.log(e.getMessage());
        }

        return result;

    }

    /**
     * Initialize the search, and thus begin the building of a search tree. If enough time, the
     * depth of the tree will be `depthLimit`.
     * @return The best move
     * @throws OutOfTimeException if we ran out of time
     */
    private Move findBestMove(Game game, List<Tuple<Move, Game>> sortedMovesAndGame, int depth)
            throws OutOfTimeException {

        int resultScore = Integer.MIN_VALUE;
        Move resultMove = null;

        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        for (var moveAndGame : sortedMovesAndGame) {

            int score = calculateScore(moveAndGame.second(), depth - 1, alpha, beta, true);

            if (score > resultScore) {
                resultScore = score;
                resultMove = moveAndGame.first();
            }

            // Update alpha for the maximizer
            alpha = Math.max(alpha, score);
        }

        return resultMove;
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

        timer.checkTime();

        List<Move> moves = evaluator.sortMovesQuicker(game);

        if (depth == 0 || !game.getPhase().equals(GamePhase.BUILD)) {

            if (!game.getPhase().equals(GamePhase.BUILD)) {
                timer.incrementBombPhasesReached();
            }

            timer.incrementNodeCount();
            return evaluator.evaluate(game, playerNumber);
        }

        boolean isMaximizer = game.getCurrentPlayerNumber() == playerNumber;

        if (isMaximizer) {

            int result = Integer.MIN_VALUE;

            // Maximizer -> Reverse so that good moves are at the beginning
            Collections.reverse(moves);

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
            Move move = moves.get(0);

            Game clonedGame = game.clone();
            clonedGame.executeMove(move);
            timer.incrementNodeCount();

            return calculateScore(clonedGame, depth - 1, alpha, beta, false);
        }
    }
}
