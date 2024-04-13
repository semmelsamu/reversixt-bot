package board;

import util.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class only stores information about what is currently on the game board, not the state of
 * the game.
 */
public class Board {

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Attributes
    |
    |-----------------------------------------------------------------------------------------------
    */

    /**
     * The dimensions of the game board. Used for quick access.
     */
    public final int width;
    public final int height;

    /**
     * The game board.
     * First dimension is the lines (y-direction), second one is columns (x-direction).
     * Starts at the top left with (0/0).
     */
    private Tile[][] tiles;

    /**
     * Every transition is stored twice, for every direction once.
     * Key: The coordinates from the outgoing field and the direction we leave the field.
     * Value: The coordinates from the field we land on when leaving the outgoing field and the
     * new direction we look at.
     */
    private Map<TransitionPart, TransitionPart> transitions;

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Constructor
    |
    |-----------------------------------------------------------------------------------------------
    */

    public Board(Tile[][] tiles, Map<TransitionPart, TransitionPart> transitions) {
        this.tiles = tiles;
        this.transitions = transitions;

        this.height = tiles.length;
        this.width = tiles[0].length;
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Getters and Setters
    |
    |-----------------------------------------------------------------------------------------------
    */

    public Tile getTile(Coordinates position) {

        if (!coordinatesLayInBoard(position)) {
            Logger.get().warn("Tried to get a tile from coordinates the board doesn't have");
            return null;
        }

        return this.tiles[position.y][position.x];
    }

    public void setTile(Coordinates coordinates, Tile tile) {

        if (!coordinatesLayInBoard(coordinates)) {
            Logger.get().error("Tried to set a tile on coordinates the board doesn't have");
            return;
        }

        this.tiles[coordinates.y][coordinates.x] = tile;
    }

    public Map<TransitionPart, TransitionPart> getTransitions() {
        return transitions;
    }

    public List<Coordinates> getAllCoordinatesWhereTileIs(Tile tile) {
        List<Coordinates> result = new LinkedList<>();

        for (int y = 0; y < height; y++) {
            Tile[] currentRow = tiles[y];
            for (int x = 0; x < width; x++) {
                Tile currentTile = currentRow[x];
                if (currentTile == tile) {
                    result.add(new Coordinates(x, y));
                }
            }
        }

        return result;
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Utility
    |
    |-----------------------------------------------------------------------------------------------
    */

    public boolean coordinatesLayInBoard(Coordinates position) {
        return !(position.x < 0 || position.y < 0 || position.x > width || position.y > height);
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   To String
    |
    |-----------------------------------------------------------------------------------------------
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
                result.append(tile.toString(true));
            }
            result.append("\n");
        }
        result.append("(Width: ").append(width).append(", height: ").append(height).append(")");
        return "Board" + "\n" + "\u001B[0m" + result;
    }
}
