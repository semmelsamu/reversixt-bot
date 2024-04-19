package game.evaluation;

import game.Game;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent an abstract rating type. Values are added to the player rating
 */
public abstract class AbstractRating {

    private final RatingType ratingType;

    private final Game game;

    private List<MapTileRating> mapTileRatings;

    private int playerRatingByCriterion;

    private int weight;

    public AbstractRating(RatingType ratingType, Game game, int weight) {
        this.mapTileRatings = new ArrayList<>();
        this.ratingType = ratingType;
        this.game = game;
        this.weight = weight;
        this.playerRatingByCriterion = 0;
    }

    public AbstractRating(RatingType ratingType, Game game) {
        this(ratingType, game, 1);
    }

    public abstract void evaluateByCriterion();

    /**
     * Can be used in every abstract method to add simple int values to the player rating
     *
     * @param playerRatingByCriterion can be added in any implementation
     */
    protected void addPlayerRatingByCriterion(int playerRatingByCriterion) {
        this.playerRatingByCriterion += playerRatingByCriterion * weight;
    }

    /**
     * Can be used in every abstract method to add simple int values to the player rating
     *
     * @param mapTileRating {@link MapTileRating} because we maybe need coordinates later
     */
    protected void addPlayerRatingByCriterion(MapTileRating mapTileRating) {
        playerRatingByCriterion += mapTileRating.value() * weight;
        this.mapTileRatings.add(mapTileRating);

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

    public int getWeight() {
        return weight;
    }
}
