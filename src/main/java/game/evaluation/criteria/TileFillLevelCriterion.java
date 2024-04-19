package game.evaluation.criteria;

import board.Tile;
import game.Game;
import game.GameStats;
import game.evaluation.AbstractRating;
import game.evaluation.RatingType;
import player.Player;

/**
 * Evaluation how much tiles the player has on the map
 */
public class TileFillLevelCriterion extends AbstractRating {

    public TileFillLevelCriterion(Game game) {
        super(RatingType.TILE_FILL_LEVEL, game);
    }

    @Override
    public void evaluateByCriterion() {
        //nicht erreichbare felder
        // globale konstanten
        /*GameStats gameStats = getGame().getGameStats();
        int allAvailableFields = getGame().getBoard().getHeight() * getGame().getBoard().getWidth();
        allAvailableFields -= gameStats.getAllCoordinatesWhereTileIs(Tile.WALL).size();

        int allPlayersTiles = 0;
        for (Player player : getGame().getPlayers()) {
            allPlayersTiles +=
                    gameStats.getAllCoordinatesWhereTileIs(player.getPlayerValue()).size();
        }
        int allTileAveragePlayers = allPlayersTiles;
        allTileAveragePlayers /= getGame().getPlayers().length;

        int ourAmountTiles = gameStats.getAllCoordinatesWhereTileIs(
                getGame().getCurrentPlayer().getPlayerValue()).size();
        if (allPlayersTiles > allAvailableFields * 0.9) {
            addPlayerRatingByCriterion((ourAmountTiles - allTileAveragePlayers) * 2);
        } else if (allPlayersTiles > allAvailableFields * 0.8) {
            addPlayerRatingByCriterion(ourAmountTiles - allTileAveragePlayers);
        } else if (allPlayersTiles > allAvailableFields * 0.7) {
            addPlayerRatingByCriterion((int) ((ourAmountTiles - allTileAveragePlayers) * 0.5));
        }
        */
    }
}
