package game.evaluation.criteria;

import board.Coordinates;
import board.Direction;
import board.TileReader;
import game.Game;
import game.evaluation.AbstractRating;
import game.evaluation.MapTileRating;
import game.evaluation.RatingType;

import java.util.List;

/**
 * Rates every tile, tiles with fewer neighbours are better
 */
public class CornerValuesCriterion extends AbstractRating {

    public CornerValuesCriterion(Game game) {
        super(RatingType.CORNER_VALUES, game);
    }

    @Override
    public void evaluateByCriterion() {
        // 8 neutral
        // durchgehende ecken
        List<Coordinates> playerTiles = getGame().getAllCoordinatesWhereTileIs(
                getGame().getCurrentPlayer().getPlayerValue());
        for(Coordinates tile : playerTiles) {
            int availableNeighbours = 0;
            for (Direction direction : Direction.values()) {
                TileReader tileReader = new TileReader(getGame(), tile, direction);
                if (!tileReader.hasNext()) {
                    continue;
                }
                availableNeighbours++;
            }
            addPlayerRatingByCriterion(new MapTileRating(tile, Math.abs(8 - availableNeighbours)));
        }
    }
}
