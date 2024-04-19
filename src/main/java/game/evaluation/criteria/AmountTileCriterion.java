package game.evaluation.criteria;

import game.Game;
import game.evaluation.AbstractRating;
import game.evaluation.RatingType;
import player.Player;

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
        int ourAmountOfTiles = getGame().getGameStats()
                .getAllCoordinatesWhereTileIs(getGame().getCurrentPlayer().getPlayerValue()).size();
        addPlayerRatingByCriterion(ourAmountOfTiles - amountTileAllPlayerAverage);

    }
}
