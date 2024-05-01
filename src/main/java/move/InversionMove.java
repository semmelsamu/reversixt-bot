package move;

import board.Coordinates;

/**
 * Invert the order of players
 */
public class InversionMove extends Move {

    public InversionMove(int player, Coordinates coordinates) {
        super(player, coordinates);
    }

    @Override
    public String toString() {
        return "InversionMove{player=" + player + ", coordinates=" + coordinates + "}";
    }
}