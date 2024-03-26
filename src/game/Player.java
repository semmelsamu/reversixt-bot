package game;

import java.util.List;

public class Player {

    private List<Stone> stones;
    private int overwriteStones;
    private int bombs;

    public Player(List<Stone> stones, int overwriteStones, int bombs) {
        this.stones = stones;
        this.overwriteStones = overwriteStones;
        this.bombs = bombs;
    }
}
