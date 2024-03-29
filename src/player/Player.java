package player;

import board.TileValue;

import java.util.List;

public class Player {

    private TileValue playerValue;
    private int overwriteStones;
    private int bombs;
    private List<Stone> stones;

    public Player(TileValue playerValue, int overwriteStones, int bombs, List<Stone> stones) {
        this.playerValue = playerValue;
        this.overwriteStones = overwriteStones;
        this.bombs = bombs;
        this.stones = stones;
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

    public List<Stone> getStones() {
        return stones;
    }
}
