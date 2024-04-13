package game.evaluation;

import game.Game;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameEvaluator {

    private int playerRating;

    private Game game;

    private Set<AbstractRating> ratings;

    public GameEvaluator(Game game) {
        this.game = game;
        this.playerRating = 0;
        ratings = new HashSet<>();
    }

    public void evaluate() {
        ratings.add(new PositionOnMapRating(game));

        for (AbstractRating rating : ratings) {
            rating.evaluateByCriterion();
            this.playerRating += rating.getPartialPlayerRating();
        }
    }

    public List<MapTileRating> getTileRatingsByRatingType(RatingType ratingType) {
        for (AbstractRating rating : ratings) {
            if(rating.getRatingType() == ratingType) {
                return rating.getMapTileRatings();
            }
        }
        return null;
    }

    public int getPlayerRating() {
        return playerRating;
    }
}
