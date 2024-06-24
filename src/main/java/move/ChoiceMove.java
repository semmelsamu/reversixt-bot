package move;

import board.Coordinates;

import java.util.Objects;

/**
 * A move where after the player set the stone, he will swap places with another player.
 */
public class ChoiceMove extends SpecialMoveInMinimaxTree {

    /**
     * The other player which the player will swap places with after he set the stone.
     */
    private final int playerToSwapWith;

    public ChoiceMove(int player, Coordinates coordinates, int playerToSwapWith) {
        super(player, coordinates);
        this.playerToSwapWith = playerToSwapWith;
    }

    /*
    |--------------------------------------------------------------------------
    | Getters
    |--------------------------------------------------------------------------
    */

    public int getPlayerToSwapWith() {
        return playerToSwapWith;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChoiceMove move = (ChoiceMove) o;
        return player == move.player && this.coordinates.equals(move.coordinates) &&
                playerToSwapWith == move.playerToSwapWith;
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, coordinates, playerToSwapWith);
    }

    @Override
    public String toString() {
        return "ChoiceMove{player=" + player + ", coordinates=" + coordinates +
                ", playerToSwapWith=" + playerToSwapWith + "}";
    }
}
