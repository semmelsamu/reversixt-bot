package player.move;

import board.Coordinates;
import player.Player;

/**
 * Invert the order of players
 */
public class InversionMove extends Move {

    public InversionMove(Player player, Coordinates coordinates) {
        super(player, coordinates);
    }

    @Override
    public String toString() {
        return "InversionMove{player=" + player + ", coordinates=" + coordinates + "}";
    }
}