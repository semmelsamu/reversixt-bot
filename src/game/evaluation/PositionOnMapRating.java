package game.evaluation;

import game.Game;

public class PositionOnMapRating extends AbstractRating{

    public PositionOnMapRating(Game game) {
        super(RatingType.POSITION_ON_MAP, 1, game);
    }

    @Override
    public void evaluateByCriterion() {

    }
}
