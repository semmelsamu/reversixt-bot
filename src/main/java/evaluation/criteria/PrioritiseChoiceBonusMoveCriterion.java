package evaluation.criteria;

import evaluation.AbstractRating;
import evaluation.RatingType;
import game.Game;

public class PrioritiseChoiceBonusMoveCriterion extends AbstractRating {

    public PrioritiseChoiceBonusMoveCriterion(Game game) {
        super(RatingType.PRIORITISE_CHOICE_BONUS, game);
    }

    @Override
    public void evaluateByCriterion() {
        /*
        Set<Move> validMoves = getGame().getValidMovesForCurrentPlayer();

        if (validMoves.isEmpty()) {
            return;
        }

        List<Coordinates> bonusTiles =
                getGame().getGameStats().getAllCoordinatesWhereTileIs(Tile.BONUS);
        List<Coordinates> choiceTiles =
                getGame().getGameStats().getAllCoordinatesWhereTileIs(Tile.CHOICE);

        if (bonusTiles.isEmpty() && choiceTiles.isEmpty()) {
            return;
        }

        List<Coordinates> choiceTilesValidMoves = validMoves.stream().map(Move::getCoordinates)
                .filter(movCor -> choiceTiles.stream().anyMatch(
                        choiceTile -> choiceTile.x == movCor.x && choiceTile.y == movCor.y))
                .toList();
        List<Coordinates> bonusTilesValidMoves = validMoves.stream().map(Move::getCoordinates)
                .filter(movCor -> bonusTiles.stream()
                        .anyMatch(bonusTile -> bonusTile.x == movCor.x && bonusTile.y == movCor.y))
                .toList();

        for (Coordinates choiceTilesValidMove : choiceTilesValidMoves) {
            addPlayerRatingByCriterion(new MapTileRating(choiceTilesValidMove, 3));
        }
        for (Coordinates bonusTilesValidMove : bonusTilesValidMoves) {
            addPlayerRatingByCriterion(new MapTileRating(bonusTilesValidMove, 3));

        }
        */
    }
}
