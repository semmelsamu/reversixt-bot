package player.move;

import board.Board;
import board.Tile;
import player.Player;
import util.ConsoleInputHandler;

/**
 * A move where after the player set the stone, he will swap places with another player.
 */
public class ChoiceMove extends Move {

    /**
     * The other player which the player will swap places with after he set the stone.
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

    @Override
    public String toString() {
        return "ChoiceMove{tile=" + tile + ", player=" + player + ", playerToSwapWith=" + playerToSwapWith + "}";
    }
}
