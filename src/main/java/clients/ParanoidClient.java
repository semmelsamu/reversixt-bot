package clients;

import game.Game;
import game.MoveCalculator;
import game.MoveExecutor;
import game.evaluation.GameEvaluator;
import player.Player;
import player.move.Move;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ParanoidClient implements Client {


    @Override
    public Move sendMove(Game game, Player player) {

        Map<Move, Integer> moveScores = new HashMap<>();

        for (Move move : (new MoveCalculator(game)).getValidMovesForPlayer(player)) {
            Game clonedGame = game.clone();
            (new MoveExecutor(clonedGame)).executeMove(move);
            moveScores.put(move, minmax(clonedGame, player, 4));
        }

        return Collections.max(moveScores.entrySet(), Map.Entry.comparingByValue()).getKey();

    }

    private int minmax(Game game, Player player, int depth) {
        if (depth == 0) {
            return (new GameEvaluator(game, player.getPlayerValue())).evaluate();
        }

        boolean max = player == game.getCurrentPlayer();

        int score = max ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int result = score;

        for (Move move : (new MoveCalculator(game)).getValidMovesForPlayer(
                game.getCurrentPlayer())) {

            Game clonedGame = game.clone();
            (new MoveExecutor(clonedGame)).executeMove(move);

            result = minmax(clonedGame, player, depth - 1);
            score = max ? Integer.max(score, result) : Integer.min(score, result);
        }

        return score;
    }

}
