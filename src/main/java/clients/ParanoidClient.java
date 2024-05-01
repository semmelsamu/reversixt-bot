package clients;

import game.Game;
import game.GamePhase;
import game.MoveCalculator;
import game.MoveExecutor;
import evaluation.GameEvaluator;
import move.Move;
import util.Logger;

import java.util.Map;

public class ParanoidClient implements Client {

    Logger logger = new Logger(this.getClass().getName());

    private long numberOfStatesVisited;

    private int depth;

    public ParanoidClient(int depth) {
        if(depth < 1) {
            throw new IllegalArgumentException("Depth must be 1 or greater");
        }
        logger.log("Launching Paranoid Client with depth limit " + depth);
        this.depth = depth;
    }

    @Override
    public Move sendMove(Game game, int player) {

        if(game.getPhase() == GamePhase.END) {
            logger.error("Move was requested but we think the game already ended");
            return null;
        }

        numberOfStatesVisited = 0;

        logger.log("Calculating new move");

        Map.Entry<Move, Integer> result = minmax(game, player, depth);

        logger.log("Visited " + numberOfStatesVisited + " possible states in");

        logger.log("Responding with " + result.getKey().getClass().getSimpleName() + result.getKey().getCoordinates() +
                " which has a score of " + result.getValue());

        return result.getKey();

    }

    private Map.Entry<Move, Integer> minmax(Game game, int player, int depth) {

        numberOfStatesVisited++;
        depth--;

        boolean max = player == game.getCurrentPlayerNumber();

        Move resultMove = null;
        int resultScore = max ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (Move move : (new MoveCalculator(game)).getValidMovesForPlayer(
                game.getCurrentPlayerNumber())) {

            Game clonedGame = game.clone();

            (new MoveExecutor(clonedGame)).executeMove(move);

            int score;
            if(depth > 0 && clonedGame.getPhase() == GamePhase.PHASE_1) {
                score = minmax(clonedGame, player, depth).getValue();
            }
            else {
                score = (new GameEvaluator(game, player)).evaluate();
            }

            if(max ? (score > resultScore) : (score < resultScore)) {
                resultScore = score;
                resultMove = move;
            }
        }

        // This should never happen because if we have no moves
        // the game would be evaluated instantly
        assert resultMove != null;

        return Map.entry(resultMove, resultScore);
    }

}
