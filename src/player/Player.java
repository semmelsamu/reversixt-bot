package player;

import board.Tile;
import board.TileValue;

import java.util.List;

public class Player {

    private TileValue playerValue;
    private int overwriteStones;
    private int bombs;
    private List<Tile> occupiedTiles;

    public Player(TileValue playerValue, int overwriteStones, int bombs, List<Tile> occupiedTiles) {
        this.playerValue = playerValue;
        this.overwriteStones = overwriteStones;
        this.bombs = bombs;
        this.occupiedTiles = occupiedTiles;
    }

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
}
