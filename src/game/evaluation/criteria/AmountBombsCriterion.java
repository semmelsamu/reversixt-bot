package game.evaluation.criteria;

import game.Game;
import game.evaluation.AbstractRating;
import game.evaluation.RatingType;
import player.Player;

/**
 * Determines if the amount of bombs in comparison to all other players
 */
public class AmountBombsCriterion extends AbstractRating {

    public AmountBombsCriterion(Game game) {
        super(RatingType.BOMBS, game);
    }

    @Override
    public void evaluateByCriterion() {
        int bombsAverage = 0;
        for (Player player : getGame().getPlayers()) {
            bombsAverage += player.getBombs();
        }
        bombsAverage /= getGame().getPlayers().length;
        int currentPlayerBombs = getGame().getCurrentPlayer().getBombs();

        int rating = currentPlayerBombs - bombsAverage;
        addPlayerRatingByCriterion(rating + getBombRadiusRating());
    }

    private int getBombRadiusRating() {
        int maxBoardlLength = Math.max(getGame().getBoard().getHeight(), getGame().getBoard().getWidth());
        maxBoardlLength /= 2;
        return getGame().getBombRadius() - maxBoardlLength;
    }
}
