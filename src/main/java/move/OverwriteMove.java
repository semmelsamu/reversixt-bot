package move;

import board.Coordinates;

/**
 * A move that uses overwrite stones.
 */
public class OverwriteMove extends Move {

    public OverwriteMove(int player, Coordinates coordinates) {
        super(player, coordinates);
    }

    @Override
    public String toString() {
        return "OverwriteMove{player=" + player + ", coordinates=" + coordinates + "}";
    }

}