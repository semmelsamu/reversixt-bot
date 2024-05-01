package evaluation.criteria;

import evaluation.AbstractRating;
import evaluation.RatingType;
import game.Game;
import game.Player;

/**
 * Determines the value amount of tiles for each player on the field
 */
public class AmountTileCriterion extends AbstractRating {

    public AmountTileCriterion(Game game) {
        super(RatingType.AMOUNT_TILE, game);
    }

    @Override
    public void evaluateByCriterion() {
        int amountTileAllPlayerAverage = 0;
        for (Player player : getGame().getPlayers()) {
            amountTileAllPlayerAverage +=
                    getGame().getGameStats().getAllCoordinatesWhereTileIs(player.getPlayerValue())
                            .size();
        }
        amountTileAllPlayerAverage /= getGame().getPlayers().length;
        // TODO: int ourAmountOfTiles = getGame().getGameStats()
        // TODO:         .getAllCoordinatesWhereTileIs(getGame().getCurrentPlayer().getPlayerValue()).size();
        // TODO: addPlayerRatingByCriterion(ourAmountOfTiles - amountTileAllPlayerAverage);

    }
}
