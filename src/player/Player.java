package player;

import board.*;
import game.MoveCalculator;
import player.move.InversionMove;
import player.move.Move;
import util.Logger;

import java.util.*;

public class Player {

    /*
    |--------------------------------------------------------------------------
    | Member variables
    |--------------------------------------------------------------------------
    */

    /**
     * The "color" of the player
     */
    private final TileValue playerValue;

    /**
     * The number of overwrite stones this player has
     */
    private int overwriteStones;

    /**
     * The number of bombs this player has
     */
    private int bombs;

    /**
     * A list of all tiles the player has occupied
     */
    private List<Tile> occupiedTiles;

    /*
    |--------------------------------------------------------------------------
    | Constructor
    |--------------------------------------------------------------------------
    */

    public Player(TileValue playerValue, int overwriteStones, int bombs, List<Tile> occupiedTiles) {
        this.playerValue = playerValue;
        this.overwriteStones = overwriteStones;
        this.bombs = bombs;
        this.occupiedTiles = occupiedTiles;
    }

    /*
    |--------------------------------------------------------------------------
    | Getters
    |--------------------------------------------------------------------------
    */

    public TileValue getPlayerValue() {
        return playerValue;
    }

    public int getOverwriteStones() {
        return overwriteStones;
    }

    public int getBombs() {
        return bombs;
    }

    public List<Tile> getOccupiedTiles() {
        return occupiedTiles;
    }

    /*
    |--------------------------------------------------------------------------
    | Setters?
    |--------------------------------------------------------------------------
    */

    public void setOccupiedTiles(List<Tile> occupiedTiles) {
        this.occupiedTiles = occupiedTiles;
    }

    public void addOccupiedTiles(Collection<Tile> newTiles){
        occupiedTiles.addAll(newTiles);
    }

    public void removeOccupiedTiles(Collection<Tile> tilesToRemove){
        occupiedTiles.removeAll(tilesToRemove);
    }

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
    public void decreaseOverwriteStones() {
        overwriteStones--;
    }

    @Override
    public String toString() {
        return "Player{" +
                "playerValue=" + playerValue +
                ", overwriteStones=" + overwriteStones +
                ", bombs=" + bombs +
                '}';
    }
}
