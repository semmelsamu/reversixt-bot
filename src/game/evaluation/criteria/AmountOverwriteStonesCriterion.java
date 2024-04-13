package game.evaluation.criteria;

import game.Game;
import game.evaluation.AbstractRating;
import game.evaluation.RatingType;
import player.Player;

/**
 * Determines if the amount of overwrite stones in comparison to all other players
 */
public class AmountOverwriteStonesCriterion extends AbstractRating {

    public AmountOverwriteStonesCriterion(Game game) {
        super(RatingType.AMOUNT_OVERWRITE_STONES, game);
    }

    @Override
    public void evaluateByCriterion() {
        int overwriteStonesAverage = 0;
        for (Player player : getGame().getPlayers()) {
            overwriteStonesAverage += player.getOverwriteStones();
        }
        overwriteStonesAverage /= getGame().getPlayers().length;
        int currentPlayerOverwriteStones = getGame().getCurrentPlayer().getOverwriteStones();

        addPlayerRatingByCriterion(currentPlayerOverwriteStones - overwriteStonesAverage);
    }
}
