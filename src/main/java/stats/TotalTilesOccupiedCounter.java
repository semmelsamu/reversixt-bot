package stats;

import board.Tile;
import game.Game;

public class TotalTilesOccupiedCounter implements Cloneable {

    short totalTilesOccupied;

    public TotalTilesOccupiedCounter(Game game) {
        for (Tile tile : Tile.getPlayerTiles(game.constants.initialPlayers())) {
            totalTilesOccupied +=
                    (short) game.coordinatesGroupedByTile.getAllCoordinatesWhereTileIs(tile).size();
        }
    }

    public short getTotalTilesOccupied() {
        return totalTilesOccupied;
    }

    public void incrementTotalTilesOccupied() {
        totalTilesOccupied++;
    }

    @Override
    public TotalTilesOccupiedCounter clone() {
        try {

            return (TotalTilesOccupiedCounter) super.clone();

        }
        catch (CloneNotSupportedException e) {
            throw new AssertionError(); // Can never happen
        }
    }
}
