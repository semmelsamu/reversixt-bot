package game.evaluation.criteria;

import game.Game;
import game.evaluation.AbstractRating;
import game.evaluation.MapTileRating;
import game.evaluation.RatingType;
import player.Player;
import player.move.Move;

import java.util.Set;

public class AmountValidMovesCriterion extends AbstractRating {

    public AmountValidMovesCriterion(Game game) {
        super(RatingType.AMOUNT_VALID_MOVES, game);
    }

    @Override
    public void evaluateByCriterion() {
        int moveAverage = 0;
        for (Player player : getGame().getPlayers()) {
            moveAverage += player.getMoves().size();
        }
        moveAverage /= getGame().getPlayers().length;

        Set<Move> movesCurrentPlayer = getGame().getCurrentPlayer().getMoves();

        for (Move move : movesCurrentPlayer) {
            addPlayerRatingByCriterion(new MapTileRating(move.getCoordinates(),
                    movesCurrentPlayer.size() - moveAverage));
        }
    }
}
