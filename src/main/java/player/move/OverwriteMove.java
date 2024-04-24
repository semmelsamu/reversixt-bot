package player.move;

import board.Coordinates;
import player.Player;

/**
 * A move that uses overwrite stones.
 */
public class OverwriteMove extends Move {

    public OverwriteMove(Player player, Coordinates coordinates) {
        super(player, coordinates);
    }

    @Override
    public String toString() {
        return "OverwriteMove{player=" + player + ", coordinates=" + coordinates + "}";
    }

}