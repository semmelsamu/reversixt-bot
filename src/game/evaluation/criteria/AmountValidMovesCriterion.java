package game.evaluation.criteria;

import game.Game;
import game.evaluation.AbstractRating;
import game.evaluation.RatingType;
import player.Player;

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
        List<Integer> movesAverages = new ArrayList<>();
        for (Player player : getGame().getPlayers()) {
            movesAverages.add(
                    player.getSizeOfValidMovesHistory().stream().mapToInt(Integer::intValue).sum() /
                            player.getSizeOfValidMovesHistory().size());

        }
        int movesAverage = movesAverages.stream().mapToInt(Integer::intValue).sum() /
                getGame().getPlayers().length;

        int moveCurrentPlayerAverage =
                getGame().getCurrentPlayer().getSizeOfValidMovesHistory().stream()
                        .mapToInt(Integer::intValue).sum() /
                        getGame().getCurrentPlayer().getSizeOfValidMovesHistory().size();

        addPlayerRatingByCriterion(moveCurrentPlayerAverage - movesAverage);
    }
}
