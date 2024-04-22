package player.move;

import board.Coordinates;
import board.Tile;

/**
 * Throw a bomb.
 */
public class BombMove extends Move {

    public BombMove(Tile player, Coordinates coordinates) {
        super(player, coordinates);
    }

    @Override
    public String toString() {
        return "BombMove{player=" + player + ", coordinates=" + coordinates + "}";
    }
}