package game;

import board.Tile;
import util.Logger;

public class Player implements Cloneable {

    private Logger logger = new Logger(this.getClass().getName());

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

    private boolean isDisqualified;

    /*
    |--------------------------------------------------------------------------
    | Constructor
    |--------------------------------------------------------------------------
    */

    public Player(Tile playerValue, int overwriteStones, int bombs) {
        this.playerValue = playerValue;
        this.overwriteStones = overwriteStones;
        this.bombs = bombs;
        this.isDisqualified = false;
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

    public boolean isDisqualified() {
        return isDisqualified;
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
        if(overwriteStones < 0) {
            throw new RuntimeException("Overwrite stones are below zero");
        }
    }

    public void decrementBombs() {
        bombs--;
    }

    public void disqualify() {
        this.isDisqualified = true;
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
