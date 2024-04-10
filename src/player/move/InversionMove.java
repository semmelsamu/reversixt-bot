package player.move;

import board.Coordinates;
import board.Tile;
import player.Player;

/**
 * Invert the order of players
 */
public class InversionMove extends Move {

    public InversionMove(Tile player, Coordinates coordinates) {
        super(player, coordinates);
    }

    @Override
    public String toString() {
        return "InversionMove{player=" + player + ", coordinates=" + coordinates + "}";
    }
}