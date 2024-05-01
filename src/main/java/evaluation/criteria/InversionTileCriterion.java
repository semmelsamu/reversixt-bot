package evaluation.criteria;

import evaluation.AbstractRating;
import evaluation.RatingType;
import game.Game;

/**
 * Handles inversion tile criterion
 */
public class InversionTileCriterion extends AbstractRating {

    public InversionTileCriterion(Game game) {
        super(RatingType.INVERSION_TILE, game);
    }

    @Override
    public void evaluateByCriterion() {
        /*
        Set<Move> validMoves = getGame().getValidMovesForCurrentPlayer();

        if (validMoves.isEmpty()) {
            return;
        }

        List<Coordinates> allInversionTiles =
                getGame().getGameStats().getAllCoordinatesWhereTileIs(Tile.INVERSION);

        List<Coordinates> inversionTilesValidMoves = validMoves.stream().map(Move::getCoordinates)
                .filter(movCor -> allInversionTiles.stream().anyMatch(
                        choiceTile -> choiceTile.x == movCor.x && choiceTile.y == movCor.y))
                .toList();

        if (inversionTilesValidMoves.isEmpty()) {
            return;
        }

        int ourPlayerRating = getGame().getGameEvaluator().getPlayerRating();
        for (int i = 1; i < getGame().getPlayers().length; i++) {
            getGame().nextPlayer();
        }
        GameEvaluator g = new GameEvaluator(getGame());
        g.removeInversionTileCriterion();
        g.evaluate();
        int beforeUsPlayerRating = g.getPlayerRating();
        getGame().nextPlayer();
        for (Coordinates inversionTilesValidMove : inversionTilesValidMoves) {
            addPlayerRatingByCriterion(new MapTileRating(inversionTilesValidMove,
                    beforeUsPlayerRating - ourPlayerRating));
        }
         */
    }
}
