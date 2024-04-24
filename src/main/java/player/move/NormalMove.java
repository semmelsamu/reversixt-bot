package player.move;

import board.Coordinates;
import player.Player;

/**
 * Invert the order of players
 */
public class NormalMove extends Move {

    public NormalMove(Player player, Coordinates coordinates) {
        super(player, coordinates);
    }

    @Override
    public String toString() {
        return "NormalMove{player=" + player + ", coordinates=" + coordinates + "}";
    }
}