package game.evaluation;

import game.Game;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRating {

    private final RatingType ratingType;

    private final Game game;

    private List<MapTileRating> mapTileRatings;

    private int playerRatingByCriterion;

    public AbstractRating(RatingType ratingType, Game game) {
        this.mapTileRatings = new ArrayList<>();
        this.ratingType = ratingType;
        this.game = game;
        this.playerRatingByCriterion = 0;
    }

    public abstract void evaluateByCriterion();

    public void addPlayerRatingByCriterion(int playerRatingByCriterion) {
        this.playerRatingByCriterion += playerRatingByCriterion;
    }

    public void addPlayerRatingByCriterion(List<MapTileRating> mapTileRatings) {
        for (MapTileRating mapTileRating : mapTileRatings) {
            playerRatingByCriterion += mapTileRating.value();
            this.mapTileRatings.addAll(mapTileRatings);
        }
    }

    public int getPlayerRatingByCriterion() {
        return playerRatingByCriterion;
    }

    public RatingType getRatingType() {
        return ratingType;
    }

    public Game getGame() {
        return game;
    }

    public List<MapTileRating> getMapTileRatings() {
        return mapTileRatings;
    }
}
