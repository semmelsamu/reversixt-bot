package player.move;

import board.Tile;
import player.Player;

/**
 * Invert the order of players
 */
public class InversionMove extends Move {

    public InversionMove(Player player, Tile tile) {
        super(player, tile);
    }

    @Override
    public String toString() {
        return "InversionMove{tile=" + tile + ", player=" + player + "}";
    }
}