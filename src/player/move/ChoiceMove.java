package player.move;

import board.Coordinates;
import board.Tile;
import player.Player;

/**
 * A move where after the player set the stone, he will swap places with another player.
 */
public class ChoiceMove extends Move {

    /**
     * The other player which the player will swap places with after he set the stone.
     */
    private final Tile playerToSwapWith;

    public ChoiceMove(Tile player, Coordinates coordinates, Tile playerToSwapWith) {
        super(player, coordinates);
        this.playerToSwapWith = playerToSwapWith;
    }

    /*
    |--------------------------------------------------------------------------
    | Getters
    |--------------------------------------------------------------------------
    */

    public Tile getPlayerToSwapWith() {
        return playerToSwapWith;
    }

    @Override
    public String toString() {
        return "ChoiceMove{player=" + player + ", coordinates=" + coordinates + ", playerToSwapWith=" + playerToSwapWith + "}";
    }
}
