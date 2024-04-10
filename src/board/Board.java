package board;

import util.Logger;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * This class only stores information about what is currently on the game board, not the state of the game.
 */
public class Board {

    /**
     * The game board.
     * First dimension is the lines (y-direction), second one is columns/rows (x-direction).
     * Starts at the top left with (0/0).
     */
    private Tile[][] tiles;

    /**
     * The dimensions of the game board. Used for quick access.
     */
    public final int width;
    public final int height;

    /*
    |--------------------------------------------------------------------------
    | Constructor
    |--------------------------------------------------------------------------
    */

    /**
     * Creates a game board from a map array and a transition array.
     */
    public Board(char[][] map, int[][] transitions) {

        Logger.get().log("Creating board");

        // Get dimensions
        height = map.length;
        width = map[0].length;
        this.tiles = new Tile[height][width];

        // Build map
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                this.tiles[y][x] = new Tile(
                        TileValue.fromChar(map[y][x]),
                        new Coordinates(x, y)
                );
            }
        }

        // Register transitions
        for (int[] transition : transitions) {

            // Transitions must be registered in both ways

            int x1 = transition[0];
            int y1 = transition[1];
            Direction d1Out = Direction.fromValue(transition[2]);
            Direction d1In = Direction.fromValue((d1Out.getValue() + 4) % 8);

            int x2 = transition[3];
            int y2 = transition[4];
            Direction d2Out = Direction.fromValue(transition[5]);
            Direction d2In = Direction.fromValue((d2Out.getValue() + 4) % 8);

            Tile tile1 = this.tiles[y1][x1];
            Tile tile2 = this.tiles[y2][x2];
        }

    }

    /*
    |--------------------------------------------------------------------------
    | Getters and setters
    |--------------------------------------------------------------------------
    */

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Tile getTile(Coordinates position) {
        return this.tiles[position.y][position.x];
    }

    public List<Tile> getAllTiles() {
        List<Tile> result = new LinkedList<>();

        for (Tile[] tileRow : tiles) {
            // Iteration could be replaced with bulk 'Collection.addAll()' call
            result.addAll(Arrays.asList(tileRow));
        }

        return result;
    }

    public List<Tile> getAllTilesWithValue(TileValue value) {

        List<Tile> allTiles = getAllTiles();
        List<Tile> result = new LinkedList<>();

        for (Tile tile : allTiles) {
            if (tile.getValue() == value)
                result.add(tile);
        }

        return result;
    }

    public void setTile(Coordinates coordinates, TileValue tileValue) {
        this.tiles[coordinates.y][coordinates.x].setValue(tileValue);
    }

    /*
    |--------------------------------------------------------------------------
    | Utility
    |--------------------------------------------------------------------------
    */

    private static String formatIntToFitLength(int number, int length) {
        return (new StringBuilder()).append(number).append(" ".repeat(length)).substring(0, length);
    }

    public String toString() {
        StringBuilder result = new StringBuilder("    ");

        // Draw x coordinates
        for (int x = 0; x < tiles[0].length; x++) {
            result.append(formatIntToFitLength(x, 3));
        }
        result.append("\n");

        for (int y = 0; y < tiles.length; y++) {
            result.append(formatIntToFitLength(y, 4));
            for (Tile tile : tiles[y]) {
                result.append(tile.getValue().toString(true));
            }
            result.append("\n");
        }
        result.append("(Width: ").append(width).append(", height: ").append(height).append(")");
        return "Board" + "\n" + "\u001B[0m" + result;
    }
}
