package game.evaluation.criteria;

import board.Coordinates;
import board.Tile;
import game.Game;
import game.evaluation.AbstractRating;
import game.evaluation.MapTileRating;
import game.evaluation.RatingType;

import java.util.List;

/**
 * Determines the value of Expansion Tiles
 */
public class ExpansionTileCriterion extends AbstractRating {

    public ExpansionTileCriterion(Game game) {
        super(RatingType.EXPANSION_TILE, game);
    }

    @Override
    public void evaluateByCriterion() {
        List<Coordinates> expansionTiles =
                getGame().getGameStats().getAllCoordinatesWhereTileIs(Tile.EXPANSION);
        if (expansionTiles.isEmpty()) {
            return;
        }
        if (getGame().getCurrentPlayer().getOverwriteStones() != 0) {
            if (getGame().getGameStats()
                    .getAllCoordinatesWhereTileIs(getGame().getCurrentPlayer().getPlayerValue())
                    .isEmpty()) {
                for (Coordinates expansionTile : expansionTiles) {
                    addPlayerRatingByCriterion(new MapTileRating(expansionTile, 10));
                }
            }
            for (Coordinates expansionTile : expansionTiles) {
                addPlayerRatingByCriterion(new MapTileRating(expansionTile, 4));
            }
        } else {
            for (Coordinates expansionTile : expansionTiles) {
                addPlayerRatingByCriterion(new MapTileRating(expansionTile, 2));
            }
        }
    }
}
