package game.evaluation;

import game.Game;
import game.evaluation.criteria.*;
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

    /**
     * Registers all criteria and adds it value to the player rating
     */
    public int evaluate() {
        registerCriteria();

        for (AbstractRating rating : ratings) {
            rating.evaluateByCriterion();
            this.playerRating += rating.getPlayerRatingByCriterion();
        }
        return this.playerRating;
    }

    private void registerCriteria() {
        ratings.add(new CornerValuesCriterion(game));
        ratings.add(new AmountOverwriteStonesCriterion(game));
        ratings.add(new AmountBombsCriterion(game));
        ratings.add(new AmountValidMovesCriterion(game));
        ratings.add(new PrioritiseChoiceBonusMoveCriterion(game));
        ratings.add(new ExpansionTileCriterion(game));
        ratings.add(new InversionTileCriterion(game));
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
    public void printRatings() {
        for (AbstractRating rating : ratings) {
            if (rating.getMapTileRatings().isEmpty()) {
                Logger.get().log(rating.getRatingType().name() + ": " +
                        rating.getPlayerRatingByCriterion());
            } else {
                Logger.get().log(rating.getRatingType().name() + ": " + rating.getMapTileRatings() +
                        " = " + rating.getPlayerRatingByCriterion());
            }
        }
    }
}
