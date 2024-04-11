package game.evaluation;

import game.Game;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameEvaluator {

    private int[][] mapRating;

    private int playerRating;

    private Game game;

    public GameEvaluator(Game game) {
        this.game = game;
        this.mapRating = new int[game.getBoard().height][game.getBoard().width];
        this.playerRating = 0;
    }

    public void evaluateMap() {
        Set<AbstractRating> ratings = new HashSet<>();
        ratings.add(new PositionOnMapRating(game));


        for (AbstractRating rating : ratings) {
            rating.evaluate();
            parseToBoard(rating.getTileRatings());
        }
    }

    private void parseToBoard(List<TileRating> tileRatings) {
        for (TileRating tileRating : tileRatings) {
            int x = tileRating.coordinates().x;
            int y = tileRating.coordinates().y;
            mapRating[x][y] += tileRating.value();
        }
    }

    public int getPlayerRating() {
        return playerRating;
    }

    public int[][] getMapRating() {
        return mapRating;
    }
}
