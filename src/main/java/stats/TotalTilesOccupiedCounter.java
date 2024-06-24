package stats;

import game.Game;
import game.Player;

public class TotalTilesOccupiedCounter implements Cloneable {

    short totalTilesOccupied;

    public TotalTilesOccupiedCounter(Game game) {
        for (Player player : game.getPlayers()) {
            totalTilesOccupied +=
                    (short) game.coordinatesGroupedByTile.getAllCoordinatesWhereTileIs(
                            player.getPlayerValue()).size();
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
