package player.move;

import board.Coordinates;
import player.Player;

/**
 * Throw a bomb.
 */
public class BombMove extends Move {

    public BombMove(Player player, Coordinates coordinates) {
        super(player, coordinates);
    }

    @Override
    public String toString() {
        return "BombMove{player=" + player + ", coordinates=" + coordinates + "}";
    }
}