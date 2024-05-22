package board;

import exceptions.CoordinatesOutOfBoundsException;
import util.Logger;
import util.Tuple;

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
        this.transitions = transitions;

        this.height = tiles.length;
        this.width = tiles[0].length;

        this.board = new byte[(int) Math.ceil((double) (width * height) / 2)];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                this.setTile(new Coordinates(x, y), tiles[x][y]);
            }
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
        var position = getPosition(coordinates);

        // If isFirst4Bits
        if (position.b()) {
            return Tile.fromByte((byte) (board[position.a()] & 0x0F));
        } else {
            return Tile.fromByte((byte) ((board[position.a()] >> 4) & 0x0F));
        }
    }

    public void setTile(Coordinates coordinates, Tile tile) {
        var position = getPosition(coordinates);

        // If isFirst4Bits
        if (position.b()) {
            board[position.a()] = (byte) ((board[position.a()] & 0xF0) | (tile.toByte() & 0x0F));
        } else {
            board[position.a()] =
                    (byte) ((board[position.a()] & 0x0F) | ((tile.toByte() << 4) & 0xF0));
        }
    }

    public Tuple<Integer, Boolean> getPosition(Coordinates coordinates) {

        if (!coordinatesLayInBoard(coordinates)) {
            throw new CoordinatesOutOfBoundsException(
                    "Tried to access coordinates the board doesn't have: " + coordinates);
        }

        int tileNumber = coordinates.y * width + coordinates.x;

        int arrayIndex = (int) Math.floor((double) tileNumber / 2);
        boolean isFirst4Bits = tileNumber % 2 == 0;

        return new Tuple<>(arrayIndex, isFirst4Bits);
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
        /* TODO
        for (int y = 0; y < height; y++) {
            Tile[] currentRow = tiles[y];
            for (int x = 0; x < width; x++) {
                Tile currentTile = currentRow[x];
                if (currentTile == tile) {
                    result.add(new Coordinates(x, y));
                }
            }
        }
        */
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
        /* TODO
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
         */
        return null;
    }
}
