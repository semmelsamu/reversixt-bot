package game.evaluation.criteria;

import game.Game;
import game.evaluation.AbstractRating;
import game.evaluation.MapTileRating;
import game.evaluation.RatingType;
import player.move.Move;

import java.util.Set;

/**
 * Compares the valid moves of each player
 */
public class AmountValidMovesCriterion extends AbstractRating {

    public AmountValidMovesCriterion(Game game) {
        super(RatingType.AMOUNT_VALID_MOVES, game, 2);
    }

    @Override
    public void evaluateByCriterion() {
        Set<Move> validMovesForCurrentPlayer = getGame().getValidMovesForCurrentPlayer();
        if (validMovesForCurrentPlayer.isEmpty()) {
            addPlayerRatingByCriterion(-2);
            return;
        }
        for (Move move : validMovesForCurrentPlayer) {
            addPlayerRatingByCriterion(new MapTileRating(move.getCoordinates(), 1));
        }
    }
}
