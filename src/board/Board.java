package board;

import util.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class only stores information about what is currently on the game board, not the state of the game.
 */
public class Board {

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
     * Key: The coordinates from the outgoing field and the direction we leave the
     * field.
     * Value: The coordinates from the field we land on when leaving the outgoing
     * field and the new direction we look at.
     */
    private Map<TransitionPart, TransitionPart> transitions;

    /*
    |--------------------------------------------------------------------------------
    |
    |   Constructor
    |
    |--------------------------------------------------------------------------------
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
                this.tiles[y][x] = Tile.fromChar(map[y][x]);
            }
        }

        // Register transitions
        for (int[] transition : transitions) {

            int x1 = transition[0];
            int y1 = transition[1];
            Coordinates coordinates1 = new Coordinates(x1, y1);
            Direction d1Out = Direction.fromValue(transition[2]);
            Direction d1In = Direction.fromValue((d1Out.getValue() + 4) % 8);

            int x2 = transition[3];
            int y2 = transition[4];
            Coordinates coordinates2 = new Coordinates(x2, y2);
            Direction d2Out = Direction.fromValue(transition[5]);
            Direction d2In = Direction.fromValue((d2Out.getValue() + 4) % 8);

            // Transitions must be registered in both ways

            this.transitions.put(
                    new TransitionPart(coordinates1, d1Out),
                    new TransitionPart(coordinates2, d2In)
            );

            this.transitions.put(
                    new TransitionPart(coordinates2, d2Out),
                    new TransitionPart(coordinates1, d1In)
            );
        }

    }

    /*
    |--------------------------------------------------------------------------------
    |
    |   Getters and Setters
    |
    |--------------------------------------------------------------------------------
    */

    public Tile getTile(Coordinates position) {
        return this.tiles[position.y][position.x];
    }

    public void setTile(Coordinates coordinates, Tile tile) {
        this.tiles[coordinates.y][coordinates.x] = tile;
    }

    public List<Coordinates> getAllCoordinatesWhereTileIs(Tile tile) {
        List<Coordinates> result = new LinkedList<>();

        for (int y = 0; y < tiles.length; y++) {
            Tile[] currentRow = tiles[y];
            for (int x = 0; x < currentRow.length; x++) {
                Tile currentTile = currentRow[x];
                if (currentTile == tile) {
                    result.add(new Coordinates(x, y));
                }
            }
        }

        return result;
    }

    /*
    |--------------------------------------------------------------------------------
    |
    |   Utility functions
    |
    |--------------------------------------------------------------------------------
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
