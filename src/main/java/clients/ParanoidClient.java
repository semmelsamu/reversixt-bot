package clients;

import game.Game;
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

    @Override
    public Move sendMove(Game game, Player player) {

        logger.log("Calculating new move");

        Set<Move> validMoves = (new MoveCalculator(game)).getValidMovesForPlayer(player);

        logger.debug(validMoves.size() + " valid move(s)");

        if (validMoves.size() == 0) {
            logger.fatal("Could not calculate any valid moves :(");
            return null;
        }

        Map<Move, Integer> moveScores = new HashMap<>();

        logger.log("Calculating minmax for every move");

        for (Move move : validMoves) {
            Game clonedGame = game.clone();
            (new MoveExecutor(clonedGame)).executeMove(move);

            int score = minmax(clonedGame, player, 1);
            logger.debug("Move " + move.getCoordinates() + " scored " + score);
            moveScores.put(move, score);
        }

        var result = Collections.max(moveScores.entrySet(), Map.Entry.comparingByValue());

        logger.log("Highest score is move on " + result.getKey().getCoordinates() +
                " with a score of " + result.getValue());

        return result.getKey();

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
