package game.evaluation.criteria;

import game.Game;
import game.evaluation.AbstractRating;
import game.evaluation.MapTileRating;
import game.evaluation.RatingType;
import player.Player;
import player.move.Move;

import java.util.ArrayList;
import java.util.List;

/**
 * Compares the valid moves of each player
 */
public class AmountValidMovesCriterion extends AbstractRating {

    public AmountValidMovesCriterion(Game game) {
        super(RatingType.AMOUNT_VALID_MOVES, game);
    }

    @Override
    public void evaluateByCriterion() {
        for (Move move : getGame().getValidMovesForCurrentPlayer()) {
            addPlayerRatingByCriterion(new MapTileRating(move.getCoordinates(), 1));
        }
    }
}
