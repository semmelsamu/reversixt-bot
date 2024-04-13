package game.evaluation.criteria;

import game.Game;
import game.evaluation.AbstractRating;
import game.evaluation.RatingType;

public class PositionOnMapRating extends AbstractRating {

    public PositionOnMapRating(Game game) {
        super(RatingType.POSITION_ON_MAP, game);
    }

    @Override
    public void evaluateByCriterion() {

    }
}
