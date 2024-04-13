package game.evaluation.criteria;

import game.Game;
import game.evaluation.AbstractRating;
import game.evaluation.RatingType;

public class CornerValuesCriterion extends AbstractRating {

    public CornerValuesCriterion(Game game) {
        super(RatingType.CORNER_VALUES, game);
    }

    @Override
    public void evaluateByCriterion() {

    }
}
