package player.move;

import board.Coordinates;
import board.Tile;

/**
 * A move that uses overwrite stones.
 */
public class OverwriteMove extends Move {

    public OverwriteMove(Tile player, Coordinates coordinates) {
        super(player, coordinates);
    }

    @Override
    public String toString() {
        return "OverwriteMove{player=" + player + ", coordinates=" + coordinates + "}";
    }

}