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

        Logger.setPriority(MoveCalculator.class.getName(), 2);

        Set<Move> validMoves = (new MoveCalculator(game)).getValidMovesForPlayer(player);

        Logger.setPriority(MoveCalculator.class.getName(), Logger.defaultPriority);

        logger.verbose(validMoves.size() + " valid move(s)");

        if (validMoves.size() == 0) {
            logger.fatal("Could not calculate any valid moves :(");
            return null;
        }

        Map<Move, Integer> moveScores = new HashMap<>();

        logger.verbose("Calculating minmax for every move");

        numberOfStatesVisited = 0;

        for (Move move : validMoves) {
            Game clonedGame = game.clone();
            (new MoveExecutor(clonedGame)).executeMove(move);

            int score = minmax(clonedGame, player, this.depth - 1);
            logger.debug("Move " + move.getCoordinates() + " scored " + score);
            moveScores.put(move, score);
        }

        logger.log("Visited " + numberOfStatesVisited + " states");

        var result = Collections.max(moveScores.entrySet(), Map.Entry.comparingByValue());

        logger.log("Responding with " + result.getKey().getClass().getSimpleName() + result.getKey().getCoordinates() +
                " which has a score of " + result.getValue());

        return result.getKey();

    }

    private int minmax(Game game, int player, int depth) {

        numberOfStatesVisited++;

        if (depth == 0) {
            return (new GameEvaluator(game, player)).evaluate();
        }

        boolean max = player == game.getCurrentPlayerNumber();

        int score = max ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int result = score;

        for (Move move : (new MoveCalculator(game)).getValidMovesForPlayer(
                game.getCurrentPlayerNumber())) {

            Game clonedGame = game.clone();
            (new MoveExecutor(clonedGame)).executeMove(move);

            result = minmax(clonedGame, player, depth - 1);
            score = max ? Integer.max(score, result) : Integer.min(score, result);
        }

        return score;
    }

}
