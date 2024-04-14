package game.evaluation.criteria;

import game.Game;
import game.evaluation.AbstractRating;
import game.evaluation.RatingType;

public class AmountValidMoves extends AbstractRating {

    public AmountValidMoves(Game game) {
        super(RatingType.AMOUNT_VALID_MOVES, game);
    }

    @Override
    public void evaluateByCriterion() {

    }
}
