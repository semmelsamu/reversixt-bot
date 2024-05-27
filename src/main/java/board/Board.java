package board;

import exceptions.CoordinatesOutOfBoundsException;
import util.Logger;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class only stores information about what is currently on the game board, not the state of
 * the game.
 */
public class Board implements Cloneable {

    Logger logger = new Logger(this.getClass().getName());

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
     * Consisting of bytes, where every byte represents two Tiles, one by the first 4 bits and one
     * by the last 4 bits. We start with the first 4 bits at the top left (0/0), the next 4 bits are
     * one to the right (0/1) and so on. If we are at the end of a line, the next 4 bits represent
     * the first Tile on the next line. If the number of  Tiles on the board is odd, the last 4 bits
     * of the last byte may be ignored as they then won't hold any information.
     */
    private byte[] board;

    /**
     * A transition consists out of two Transition parts, which are linked together. Transitions can
     * be considered "Portals": If we enter a Transition's part, we exit its counterpart.
     * -
     * For the sake of performance, we store each transition twice, once as is and once flipped, so
     * we can iterate through only the keys of the Map and still meet every Transition part.
     * -
     * Transition Parts are stored as shorts, which can be interpreted as follows:
     * - The first 4 Bits represent the Direction (0=North, 1=Northeast, ...)
     * - The next 6 Bits represent the X Coordinate
     * - The last 6 Bits represent the Y Coordinate
     * A short provides enough space, as: We have 8 directions (less than 2^4=16) and a board's size
     * will never exceed 50x50 (less than 2^6=64)
     */
    private Map<Short, Short> transitions;

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Constructor
    |
    |-----------------------------------------------------------------------------------------------
    */

    public Board(Tile[][] tiles, Map<TransitionPart, TransitionPart> transitions) {

        this.height = tiles.length;
        this.width = tiles[0].length;

        this.board = new byte[(int) Math.ceil((double) (width * height) / 2)];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                this.setTile(new Coordinates(x, y), tiles[y][x]);
            }
        }

        this.transitions = new HashMap<>();

        for(var transition : transitions.entrySet()) {
            this.transitions.put(transition.getKey().toShort(), transition.getValue().toShort());
        }
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Getters and Setters
    |
    |-----------------------------------------------------------------------------------------------
    */

    public Tile getTile(Coordinates coordinates) {
        if (!coordinatesLayInBoard(coordinates)) {
            throw new CoordinatesOutOfBoundsException(
                    "Tried to access coordinates the board doesn't have: " + coordinates);
        }

        int tileNumber = coordinates.y * width + coordinates.x;

        int arrayIndex = (int) Math.floor((double) tileNumber / 2);
        boolean isFirst4Bits = tileNumber % 2 == 0;

        if (isFirst4Bits) {
            return Tile.fromByte((byte) (board[arrayIndex] & 0x0F));
        } else {
            return Tile.fromByte((byte) ((board[arrayIndex] >> 4) & 0x0F));
        }
    }

    public void setTile(Coordinates coordinates, Tile tile) {
        if (!coordinatesLayInBoard(coordinates)) {
            throw new CoordinatesOutOfBoundsException(
                    "Tried to access coordinates the board doesn't have: " + coordinates);
        }

        int tileNumber = coordinates.y * width + coordinates.x;

        int arrayIndex = (int) Math.floor((double) tileNumber / 2);
        boolean isFirst4Bits = tileNumber % 2 == 0;

        if (isFirst4Bits) {
            board[arrayIndex] = (byte) ((board[arrayIndex] & 0xF0) | (tile.toByte() & 0x0F));
        } else {
            board[arrayIndex] =
                    (byte) ((board[arrayIndex] & 0x0F) | ((tile.toByte() << 4) & 0xF0));
        }
    }

    public Map<Short, Short> getTransitions() {
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
            for (int x = 0; x < width; x++) {
                if (getTile(new Coordinates(x, y)) == tile) {
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
        for (int x = 0; x < width; x++) {
            result.append(formatIntToFitLength(x, 3));
        }
        result.append("\n");

        for (int y = 0; y < height; y++) {
            result.append(formatIntToFitLength(y, 4));
            for (int x = 0; x < width; x++) {
                result.append(getTile(new Coordinates(x, y)).toString(true));
            }
            result.append("\n");
        }
        result.append("(Width: ").append(width).append(", height: ").append(height).append(")");

        StringBuilder raw = new StringBuilder("Raw: ");

        for (byte b : board) {
            raw.append(Integer.toHexString(b & 0x0F)).append(Integer.toHexString((b >> 4) & 0x0F));
        }

        return "Board" + "\n" + "\u001B[0m" + result + "\n" + raw;
    }

    @Override
    public Board clone() {
        try {
            Board clone = (Board) super.clone();
            clone.board = this.board.clone();
            clone.transitions = new HashMap<>();
            clone.transitions.putAll(transitions);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
