package clients;

import game.Game;
import game.GamePhase;
import game.MoveCalculator;
import game.MoveExecutor;
import game.evaluation.GameEvaluator;
import player.Player;
import player.move.Move;
import util.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ParanoidClient implements Client {

    Logger logger = new Logger(this.getClass().getName());

    private static long numberOfStatesVisited;

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

        logger.log("Calculating new move");

        numberOfStatesVisited = 0;

        Map.Entry<Move, Integer> result = minmax(game, player, depth);

        logger.log("Responding with " + result.getKey().getClass().getSimpleName() + result.getKey().getCoordinates() +
                " which has a score of " + result.getValue());

        return result.getKey();

    }

    private Map.Entry<Move, Integer> minmax(Game game, int player, int depth) {

        numberOfStatesVisited++;
        depth--;

        boolean max = player == game.getCurrentPlayerNumber();

        Move bestMove = null;
        int score = max ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (Move move : (new MoveCalculator(game)).getValidMovesForPlayer(
                game.getCurrentPlayerNumber())) {

            Game clonedGame = game.clone();
            (new MoveExecutor(clonedGame)).executeMove(move);

            int result;
            if(depth > 0 && clonedGame.getPhase() == GamePhase.END) {
                result = minmax(clonedGame, player, depth).getValue();
            }
            else {
                result = (new GameEvaluator(game, player)).evaluate();
            }

            if(max ? (result > score) : (result < score)) {
                score = result;
                bestMove = move;
            }
        }

        return Map.entry(bestMove, score);
    }

}
