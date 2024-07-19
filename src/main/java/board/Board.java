package board;

import exceptions.CoordinatesOutOfBoundsException;
import util.StringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * This class only stores information about what is currently on the game board, not the state of
 * the game.
 */
public class Board implements Cloneable {

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
    private final short width;
    private final short height;

    /**
     * The game board. Consisting of bytes, where every byte represents two Tiles, one by the first
     * 4 bits and one by the last 4 bits. We start with the first 4 bits at the top left (0/0), the
     * next 4 bits are one to the right (0/1) and so on. If we are at the end of a line, the next 4
     * bits represent the first Tile on the next line. If the number of  Tiles on the board is odd,
     * the last 4 bits of the last byte may be ignored as they then won't hold any information.
     */
    private byte[] board;

    /**
     * A transition consists of two Transition parts, which are linked together. Transitions can be
     * considered "Portals": If we enter a Transition's part, we exit its counterpart.
     * <p>
     * For the sake of performance, we store each transition twice, once as is and once flipped, so
     * we can iterate through only the keys of the Map and still meet every Transition part.
     * <p>
     * Transition Parts are stored as shorts, which can be interpreted as follows:
     * <ul>
     * <li>The first 4 Bits represent the Direction (0=North, 1=Northeast, ...)</li>
     * <li>The next 6 Bits represent the X Coordinate</li>
     * <li>The last 6 Bits represent the Y Coordinate</li>
     * </ul>
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

        this.height = (short) tiles.length;
        this.width = (short) tiles[0].length;

        this.board = new byte[(int) Math.ceil((double) (width * height) / 2)];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                this.setTile(new Coordinates(x, y), tiles[y][x]);
            }
        }

        this.transitions = new HashMap<>();

        for (var transition : transitions.entrySet()) {
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
            board[arrayIndex] = (byte) ((board[arrayIndex] & 0x0F) | ((tile.toByte() << 4) & 0xF0));
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

    public String toString() {
        StringBuilder result = new StringBuilder(StringUtil.ANSI_RESET + "Y\\X ");

        // Draw x coordinates
        result.append(StringUtil.ANSI_GRAY + StringUtil.ANSI_ITALIC);
        for (int x = 0; x < width; x++) {
            result.append(StringUtil.formatIntToFitLength(x, 3));
        }
        result.append("\n");

        for (int y = 0; y < height; y++) {
            // Draw y coordinate
            result.append(StringUtil.ANSI_GRAY + StringUtil.ANSI_ITALIC)
                    .append(StringUtil.formatIntToFitLength(y, 4)).append(StringUtil.ANSI_RESET);

            // Draw tiles
            for (int x = 0; x < width; x++) {
                result.append(getTile(new Coordinates(x, y)).toString(true));
            }

            result.append(StringUtil.ANSI_RESET).append("\n");
        }

        return "Board" + "\n" + "\u001B[0m" + result + "\n"; //+ raw;
    }

    @Override
    public Board clone() {
        try {
            Board clone = (Board) super.clone();
            clone.board = this.board.clone();
            return clone;
        }
        catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
