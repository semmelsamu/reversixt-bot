package game.evaluation;

import board.Coordinates;
import game.Game;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRating {

    private final RatingType ratingType;

    private final Game game;

    private final int weight;

    private List<TileRating> tileRatings;

    public AbstractRating(RatingType ratingType, int weight, Game game) {
        this.tileRatings = new ArrayList<>();
        this.ratingType = ratingType;
        this.weight = weight;
        this.game = game;
    }

    public abstract void evaluate();

    public RatingType getRatingType() {
        return ratingType;
    }

    public Game getGame() {
        return game;
    }

    public List<TileRating> getTileRatings() {
        return tileRatings;
    }

    public void addValue(Coordinates coordinates, int value) {
        tileRatings.add(new TileRating(coordinates, value * weight));
    }
}
