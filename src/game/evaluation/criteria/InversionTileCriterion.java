package game.evaluation.criteria;

import board.Coordinates;
import board.Tile;
import game.Game;
import game.evaluation.AbstractRating;
import game.evaluation.MapTileRating;
import game.evaluation.RatingType;
import player.move.Move;

import java.util.List;
import java.util.Set;

/**
 * Handles inversion tile criterion
 */
public class InversionTileCriterion extends AbstractRating {

    public InversionTileCriterion(Game game) {
        super(RatingType.INVERSION_TILE, game);
    }

    @Override
    public void evaluateByCriterion() {
        Set<Move> validMoves = getGame().getValidMovesForCurrentPlayer();

        if (validMoves.isEmpty()) {
            return;
        }

        List<Coordinates> allInversionTiles =
                getGame().getGameStats().getAllCoordinatesWhereTileIs(Tile.INVERSION);

        List<Coordinates> inversionTilesValidMoves = validMoves.stream().map(Move::getCoordinates)
                .filter(movCor -> allInversionTiles.stream().allMatch(
                        choiceTile -> choiceTile.x == movCor.x && choiceTile.y == movCor.y))
                .toList();

        if (inversionTilesValidMoves.isEmpty()) {
            return;
        }

        int ourPlayerRating = getGame().getGameEvaluator().getPlayerRating();
        for (int i = 1; i < getGame().getPlayers().length; i++) {
            getGame().nextPlayer();
        }
        int beforeUsPlayerRating = getGame().getGameEvaluator().getPlayerRating();
        if (ourPlayerRating >= beforeUsPlayerRating) {
            for (Coordinates inversionTilesValidMove : inversionTilesValidMoves) {
                addPlayerRatingByCriterion(new MapTileRating(inversionTilesValidMove, -1));
            }
        } else {
            for (Coordinates inversionTilesValidMove : inversionTilesValidMoves) {
                addPlayerRatingByCriterion(new MapTileRating(inversionTilesValidMove, 2));
            }
        }
    }
}
