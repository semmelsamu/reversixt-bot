package board;

import util.Logger;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class only stores information about what is currently on the game board, not the state of
 * the game.
 */
public class Board implements Cloneable{

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
    private final int width;
    private final int height;

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
            Logger.get().debug("Not tile is found, check for transitions.");
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

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
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
        return position.x >= 0 && position.y >= 0 && position.x < width && position.y < height;
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

    @Override
    public Board clone() {
        try {
            Board clone = (Board) super.clone();

            clone.tiles = new Tile[height][width];
            for (int y = 0; y < height; y++) {
                System.arraycopy(tiles[y], 0, clone.tiles[y], 0, width);
            }

            clone.transitions = new HashMap<>();
            for (Map.Entry<TransitionPart, TransitionPart> entry : transitions.entrySet()) {
                clone.transitions.put(entry.getKey().clone(), entry.getValue().clone());
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
