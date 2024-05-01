package evaluation.criteria;

import evaluation.AbstractRating;
import evaluation.RatingType;
import game.Game;

/**
 * Compares the valid moves of each player
 */
public class AmountValidMovesCriterion extends AbstractRating {

    public AmountValidMovesCriterion(Game game) {
        super(RatingType.AMOUNT_VALID_MOVES, game);
    }

    @Override
    public void evaluateByCriterion() {
        /*
        Set<Move> validMovesForCurrentPlayer = getGame().getValidMovesForCurrentPlayer();
        if (validMovesForCurrentPlayer.isEmpty()) {
            addPlayerRatingByCriterion(-2);
            return;
        }
        for (Move move : validMovesForCurrentPlayer) {
            addPlayerRatingByCriterion(new MapTileRating(move.getCoordinates(), 1));
        }
        */
    }

}
