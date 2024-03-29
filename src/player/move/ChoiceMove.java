package player.move;

import board.Tile;
import player.Player;

/**
 * A move where after the player set the stone, he will swap places with another player.
 */
public class ChoiceMove extends Move {

    /**
     * The other player the player will swap places with after he set the stone.
     */
    private final Player playerToSwapWith;

    public ChoiceMove(Player player, Tile tile, Player playerToSwapWith) {
        super(player, tile);
        this.playerToSwapWith = playerToSwapWith;
    }

    /*
    |--------------------------------------------------------------------------
    | Getters
    |--------------------------------------------------------------------------
    */

    public Player getPlayerToSwapWith() {
        return playerToSwapWith;
    }
}
