package player.move;

import board.Coordinates;
import board.Tile;

/**
 * A most basic move where the player only sets a stone.
 */
public class Move implements Comparable<Move> {

    /**
     * The player this move belongs to.
     */
    protected final Tile player;

    /**
     * The tile the move targets.
     */
    protected final Coordinates coordinates;

    public Move(Tile player, Coordinates coordinates) {
        this.player = player;
        this.coordinates = coordinates;
    }

    /*
    |--------------------------------------------------------------------------
    | Getters
    |--------------------------------------------------------------------------
    */

    public Tile getPlayer() {
        return player;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    @Override
    public int compareTo(Move that) {
        if (this.coordinates.x != that.coordinates.x) {
            return Integer.compare(this.coordinates.x, that.coordinates.x);
        } else {
            return Integer.compare(this.coordinates.y, that.coordinates.y);
        }
    }

    @Override
    public String toString() {
        return "Move{player=" + player + ", coordinates=" + coordinates + "}";
    }
}
