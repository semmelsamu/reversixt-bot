package player.move;

import board.Board;
import board.Tile;
import player.Player;

import java.util.Set;

/**
 * Invert the order of players
 */
public class InversionMove extends Move {

    public InversionMove(Player player, Tile tile) {
        super(player, tile);
    }
}