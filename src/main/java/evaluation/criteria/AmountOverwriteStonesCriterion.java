package evaluation.criteria;

import evaluation.AbstractRating;
import evaluation.RatingType;
import game.Game;
import game.Player;

/**
 * Determines if the amount of overwrite stones in comparison to all other players
 */
public class AmountOverwriteStonesCriterion extends AbstractRating {

    public AmountOverwriteStonesCriterion(Game game) {
        super(RatingType.AMOUNT_OVERWRITE_STONES, game, 3);
    }

    @Override
    public void evaluateByCriterion() {
        int overwriteStonesAverage = 0;
        for (Player player : getGame().getPlayers()) {
            overwriteStonesAverage += player.getOverwriteStones();
        }
        overwriteStonesAverage /= getGame().getPlayers().length;
        // TODO: int currentPlayerOverwriteStones = getGame().getCurrentPlayer().getOverwriteStones();

        // TODO: addPlayerRatingByCriterion(currentPlayerOverwriteStones - overwriteStonesAverage);
    }
}
