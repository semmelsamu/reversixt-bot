package clients;

import evaluation.GameEvaluator;
import game.Game;
import game.GamePhase;
import game.MoveCalculator;
import game.MoveExecutor;
import move.Move;
import util.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ParanoidClient implements Client {

    Logger logger = new Logger(this.getClass().getName());

    private long stats_totalTime;
    private long numberOfStatesVisited;
    private long numberOfGamesEvaluated;

    private List<Integer> branchingFactors;

    private int depth;

    public ParanoidClient(int depth) {
        if (depth < 1) {
            throw new IllegalArgumentException("Depth must be 1 or greater");
        }
        logger.log("Launching Paranoid Client with depth limit " + depth);
        this.depth = depth;
    }

    @Override
    public Move sendMove(Game game, int player) {

        if (game.getPhase() == GamePhase.END) {
            logger.error("Move was requested but we think the game already ended");
            return null;
        }

        stats_totalTime = System.nanoTime();
        numberOfStatesVisited = 0;
        numberOfGamesEvaluated = 0;
        branchingFactors = new LinkedList<>();

        logger.log("Calculating new move");

        Map.Entry<Move, Integer> result = minmax(game, player, depth);

        logger.log("Done (" + ((System.nanoTime() - stats_totalTime) / 1_000_000) + "ms)");
        logger.verbose("Visited " + numberOfStatesVisited + " possible states");
        logger.verbose("Evaluated " + numberOfGamesEvaluated + " games");
        logger.verbose("Average branching factor: " +
                branchingFactors.stream().mapToInt(Integer::intValue).average().orElse(0.0));

        logger.log("Responding with " + result.getKey().getClass().getSimpleName() +
                result.getKey().getCoordinates() + " which has a score of " + result.getValue());

        return result.getKey();

    }

    private Map.Entry<Move, Integer> minmax(Game game, int player, int depth) {

        depth--;

        boolean max = player == game.getCurrentPlayerNumber();

        Move resultMove = null;
        int resultScore = max ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        Set<Move> possibleMoves = MoveCalculator.getValidMovesForPlayer(game, game.getCurrentPlayerNumber());

        branchingFactors.add(possibleMoves.size());

        for (Move move : possibleMoves) {

            Game clonedGame = game.clone();

            MoveExecutor.executeMove(clonedGame, move);
            numberOfStatesVisited++;

            int score;
            if (depth > 0 && clonedGame.getPhase() == GamePhase.PHASE_1) {
                score = minmax(clonedGame, player, depth).getValue();
            } else {
                score = GameEvaluator.evaluate(game, player);
                numberOfGamesEvaluated++;
            }

            if (max ? (score > resultScore) : (score < resultScore)) {
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
