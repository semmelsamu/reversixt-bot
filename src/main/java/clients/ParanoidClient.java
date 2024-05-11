package clients;

import evaluation.GameEvaluator;
import game.Game;
import game.GamePhase;
import move.Move;
import util.Logger;

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

        if (game.getValidMovesForCurrentPlayer().isEmpty()) {
            throw new RuntimeException("Could not calculate any possible moves");
        }

        logger.debug(game.getValidMovesForCurrentPlayer().size() + " possible moves");

        int resultScore = Integer.MIN_VALUE;
        Move resultMove = null;

        for (Move move : game.getValidMovesForCurrentPlayer()) {

            Game clonedGame = game.clone();
            clonedGame.executeMove(move);
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

        for (Move move : game.getValidMovesForCurrentPlayer()) {
            Game clonedGame = game.clone();
            clonedGame.executeMove(move);
            int score = minmax(clonedGame, depth - 1);
            result = isMaximizer ? Math.max(result, score) : Math.min(result, score);

        }

        return result;
    }
}
