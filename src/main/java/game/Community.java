package game;

import board.Tile;

import java.util.Set;

public class Community {

    private Set<Tile> tiles;
    private Set<Integer> players;
    private boolean relevant;

    public Community(Set<Integer> players, Set<Tile> tiles) {
        this.players = players;
        this.tiles = tiles;
        this.relevant = false;
    }
}
