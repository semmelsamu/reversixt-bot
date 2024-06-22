package stats;

import board.Tile;
import game.Game;

public class TotalTilesOccupiedCounter {

    short totalTilesOccupied;

    public TotalTilesOccupiedCounter(Game game) {
        for (Tile tile : Tile.getPlayerTiles(game.constants.initialPlayers())) {
            totalTilesOccupied += (short) game.stats.getAllCoordinatesWhereTileIs(tile).size();
        }
    }

    public short getTotalTilesOccupied() {
        return totalTilesOccupied;
    }

    public void incrementTotalTilesOccupied() {
        totalTilesOccupied++;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
