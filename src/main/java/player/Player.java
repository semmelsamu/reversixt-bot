package player;

import board.Tile;

import java.util.Objects;

public class Player implements Cloneable {

    /*
    |--------------------------------------------------------------------------
    | Member variables
    |--------------------------------------------------------------------------
    */

    /**
     * The "color" of the player
     */
    private final Tile playerValue;

    /**
     * The number of overwrite stones this player has
     */
    private int overwriteStones;

    /**
     * The number of bombs this player has
     */
    private int bombs;

    /*
    |--------------------------------------------------------------------------
    | Constructor
    |--------------------------------------------------------------------------
    */

    public Player(Tile playerValue, int overwriteStones, int bombs) {
        this.playerValue = playerValue;
        this.overwriteStones = overwriteStones;
        this.bombs = bombs;
    }

    /*
    |--------------------------------------------------------------------------
    | Getters
    |--------------------------------------------------------------------------
    */

    public Tile getPlayerValue() {
        return playerValue;
    }

    public int getOverwriteStones() {
        return overwriteStones;
    }

    public int getBombs() {
        return bombs;
    }

    /*
    |--------------------------------------------------------------------------
    | Setters?
    |--------------------------------------------------------------------------
    */

    /**
     * Increment overwrite stones by 1
     */
    public void incrementOverwriteStone() {
        overwriteStones++;
    }

    /**
     * Increment bombs by 1
     */
    public void incrementBombs() {
        bombs++;
    }

    /**
     * Decrement overwrite stones by 1
     */
    public void decrementOverwriteStones() {
        overwriteStones--;
    }

    @Override
    public String toString() {
        return "Player{" + "playerValue=" + playerValue + ", overwriteStones=" + overwriteStones +
                ", bombs=" + bombs + '}';
    }

    @Override
    public Player clone() {
        try {
            return (Player) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Player player = (Player) o;
        return playerValue == player.playerValue;
    }

}
