package player.move;

import board.Tile;
import player.Player;

/**
 * A most basic move where the player only sets a stone.
 */
public class Move {

    /**
     * The player this move belongs to.
     */
    private final Player player;

    /**
     * The tile the move targets.
     */
    private final Tile tile;

    public Move(Player player, Tile tile) {
        this.player = player;
        this.tile = tile;
    }

    /*
    |--------------------------------------------------------------------------
    | Getters
    |--------------------------------------------------------------------------
    */

    public Player getPlayer() {
        return player;
    }

    public Tile getTile() {
        return tile;
    }
}
