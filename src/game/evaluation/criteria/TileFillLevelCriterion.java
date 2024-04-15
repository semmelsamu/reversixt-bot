package game.evaluation.criteria;

import board.Tile;
import game.Game;
import game.GameStats;
import game.evaluation.AbstractRating;
import game.evaluation.RatingType;

/**
 * Evaluation how much tiles the player has on the map
 */
public class TileFillLevelCriterion extends AbstractRating {

    public TileFillLevelCriterion(Game game) {
        super(RatingType.TILE_FILL_LEVEL, game);
    }

    @Override
    public void evaluateByCriterion() {
        GameStats gameStats = getGame().getGameStats();
        int allAvailableFields = getGame().getBoard().getHeight() * getGame().getBoard().getWidth();
        allAvailableFields -= gameStats.getAllCoordinatesWhereTileIs(Tile.WALL).size();

        int currentPlayerFillLevel = gameStats.getAllCoordinatesWhereTileIs(getGame().getCurrentPlayer().getPlayerValue()).size();
        if(currentPlayerFillLevel > allAvailableFields * 0.9) {
            addPlayerRatingByCriterion(30);
        }else if(currentPlayerFillLevel > allAvailableFields * 0.8) {
            addPlayerRatingByCriterion(15);
        }else if(currentPlayerFillLevel > allAvailableFields * 0.7) {
            addPlayerRatingByCriterion(5);
        }
    }
}
