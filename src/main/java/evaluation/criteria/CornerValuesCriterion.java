package evaluation.criteria;

import evaluation.AbstractRating;
import evaluation.RatingType;
import game.Game;

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
        // TODO: List<Coordinates> playerTiles = getGame().getAllCoordinatesWhereTileIs(
        /*        getGame().getCurrentPlayer().getPlayerValue());
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
        }*/
    }
}
