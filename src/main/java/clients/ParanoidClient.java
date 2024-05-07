package clients;

import evaluation.GameEvaluator;
import game.Game;
import game.GamePhase;
import game.MoveCalculator;
import game.MoveExecutor;
import move.Move;
import util.Logger;

import java.util.Set;

public class ParanoidClient extends Client {

    Logger logger = new Logger(this.getClass().getName());

    public ParanoidClient() {
        logger.log("Launching Paranoid Client");
    }

    @Override
    public Move sendMove(int timeLimit, int depthLimit) {

        if (game.getPhase() == GamePhase.END) {
            throw new RuntimeException("Move was requested but we think the game already ended");
        }

        Set<Move> possibleMoves = MoveCalculator.getValidMovesForPlayer(game, ME);

        if (possibleMoves.isEmpty()) {
            throw new RuntimeException("Could not calculate any possible moves");
        }

        logger.debug(possibleMoves.size() + " possible moves");

        int resultScore = Integer.MIN_VALUE;
        Move resultMove = null;

        for (Move move : possibleMoves) {

            Game clonedGame = game.clone();
            MoveExecutor.executeMove(clonedGame, move);
            int score = minmax(clonedGame, depthLimit);

            if (score > resultScore) {
                resultScore = score;
                resultMove = move;
            }
        }

        logger.log("Responding with " + resultMove.getClass().getSimpleName() +
                resultMove.getCoordinates() + " which has a score of " + resultScore);

        return resultMove;
    }

    private int minmax(Game game, int depth) {

        if (depth == 0 || game.getPhase() != GamePhase.PHASE_1) {
            return GameEvaluator.evaluate(game, ME);
        }

        int currentPlayerNumber = game.getCurrentPlayerNumber();

        boolean isMaximizer = currentPlayerNumber == ME;

        int result = isMaximizer ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (Move move : MoveCalculator.getValidMovesForPlayer(game, currentPlayerNumber)) {

            Game clonedGame = game.clone();
            MoveExecutor.executeMove(clonedGame, move);
            int score = minmax(clonedGame, depth - 1);

            if (isMaximizer) {
                if (score > result) {
                    result = score;
                }
            } else {
                if (score < result) {
                    result = score;
                }
            }

        }

        return result;
    }
}
