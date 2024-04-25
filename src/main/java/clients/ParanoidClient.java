package clients;

import game.Game;
import game.MoveCalculator;
import game.MoveExecutor;
import game.evaluation.GameEvaluator;
import player.Player;
import player.move.Move;

public class ParanoidClient implements Client {


    @Override
    public Move sendMove(Game game, Player player) {


    }

    private int minmax(Game game, Player player, int depth) {
        if (depth == 0) {
            return (new GameEvaluator(game)).evaluate();
        }

        boolean max = player == game.getCurrentPlayer();

        int score = max ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int result = score;

        for (Move move : (new MoveCalculator(game)).getValidMovesForPlayer(
                game.getCurrentPlayer())) {

            Game clonedGame = game.clone();
            (new MoveExecutor(clonedGame)).executeMove(move);

            result = minmax(clonedGame, player, depth-1);
            score = max ? Integer.max(score, result) : Integer.min(score, result);
        }

        return score;
    }

}
