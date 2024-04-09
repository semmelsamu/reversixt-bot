package player.move;

import board.Coordinates;
import board.Tile;
import player.Player;

/**
 * A most basic move where the player only sets a stone.
 */
public class Move implements Comparable<Move> {

    /**
     * The player this move belongs to.
     */
    protected final Player player;

    /**
     * The tile the move targets.
     */
    protected final Tile tile;

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

    @Override
    public int compareTo(Move o) {
        Coordinates thisPosition = this.getTile().getPosition();
        Coordinates otherPosition = o.getTile().getPosition();
        if (thisPosition.x != otherPosition.x) {
            return Integer.compare(thisPosition.x, otherPosition.x);
        } else {
            return Integer.compare(thisPosition.y, otherPosition.y);
        }
    }

    @Override
    public String toString() {
        return "Move{tile=" + tile + ", player=" + player + "}";
    }
}
