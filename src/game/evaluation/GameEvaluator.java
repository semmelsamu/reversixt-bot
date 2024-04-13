package game.evaluation;

import game.Game;
import game.evaluation.criteria.PositionOnMapRating;
import util.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents the evaluation logic
 */
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
            this.playerRating += rating.getPlayerRatingByCriterion();
        }
    }

    /**
     * Returns the list of {@link MapTileRating} by {@link RatingType}
     * //TODO: may be unnecessary
     *
     * @param ratingType different types of ratings
     * @return list or null if not type found
     */
    public List<MapTileRating> getTileRatingsByRatingType(RatingType ratingType) {
        for (AbstractRating rating : ratings) {
            if (rating.getRatingType() == ratingType) {
                return rating.getMapTileRatings();
            }
        }
        return null;
    }

    public int getPlayerRating() {
        return playerRating;
    }

    /**
     * Prints the player ratings by type
     */
    public void printRatings(){
        for (AbstractRating rating : ratings) {
            Logger.get().log(rating.getRatingType().name() + ": " + rating.getPlayerRatingByCriterion());
        }
    }
}
