package move;

import board.Coordinates;

/**
 * Invert the order of players
 */
public class NormalMove extends Move {

    public NormalMove(int player, Coordinates coordinates) {
        super(player, coordinates);
    }

    @Override
    public String toString() {
        return "NormalMove{player=" + player + ", coordinates=" + coordinates + "}";
    }
}