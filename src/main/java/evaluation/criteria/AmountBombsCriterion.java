package evaluation.criteria;

import evaluation.AbstractRating;
import evaluation.RatingType;
import game.Game;
import game.Player;

/**
 * Determines if the amount of bombs in comparison to all other players
 */
public class AmountBombsCriterion extends AbstractRating {

    public AmountBombsCriterion(Game game) {
        super(RatingType.BOMBS, game, 1);
        // weight: bombRadius * bombRadius
    }

    @Override
    public void evaluateByCriterion() {
        int bombsAverage = 0;
        for (Player player : getGame().getPlayers()) {
            bombsAverage += player.getBombs();
        }
        bombsAverage /= getGame().getPlayers().length;
        // TODO: int currentPlayerBombs = getGame().getCurrentPlayer().getBombs();

        // TODO: addPlayerRatingByCriterion(currentPlayerBombs - bombsAverage);
    }
}
