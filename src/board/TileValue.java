package board;

import java.util.Arrays;
import java.util.List;

/**
 * Formerly TileType
 */
public enum TileValue {

    /*
    |--------------------------------------------------------------------------
    | Values
    |--------------------------------------------------------------------------
    */

    EMPTY('0'),
    PLAYER1('1'),
    PLAYER2('2'),
    PLAYER3('3'),
    PLAYER4('4'),
    PLAYER5('5'),
    PLAYER6('6'),
    PLAYER7('7'),
    PLAYER8('8'),
    WALL('-'),
    CHOICE('c'),
    INVERSION('i'),
    BONUS('b'),
    EXPANSION('x');

    /*
    |--------------------------------------------------------------------------
    | Char conversion logic
    |--------------------------------------------------------------------------
    */

    /**
     * The value of the tile, stored as a character.
     */
    public final char character;

    /**
     * Returns the enum from a character.
     */
    public static TileValue fromChar(char c) {
        for (TileValue type : TileValue.values()) {
            if (type.character == c) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown character: " + c);
    }

    /**
     * Creates a new enum from a character.
     */
    TileValue(char character) {
        this.character = character;
    }

    /*
    |--------------------------------------------------------------------------
    | Information functions
    |--------------------------------------------------------------------------
    */

    /**
     * States if the tile is expandable, i.e. a player can expand to it.
     */
    public boolean isExpandable() {
        return TileValue.fromChar(this.character) != WALL;
    }

    /**
     * States if the tile is empty. Inversion, bonus and choice fields are also considered empty.
     */
    public boolean isEmpty() {
        TileValue value = TileValue.fromChar(this.character);
        return (value == EMPTY || value == BONUS || value == INVERSION || value == CHOICE);
    }

    /**
     * States if the tile is occupied by a player.
     */
    public boolean isPlayer() {
        TileValue value = TileValue.fromChar(this.character);
        return (value == PLAYER1 || value == PLAYER2 || value == PLAYER3 || value == PLAYER4 || value == PLAYER5 || value == PLAYER6);
    }

    /*
    |--------------------------------------------------------------------------
    | Utility
    |--------------------------------------------------------------------------
    */

    public String toString(boolean useColors) {
        if (!useColors) return String.valueOf(character);

        return switch (character) {
            case '0' -> " . ";
            case '-' -> "\u001B[47m   \u001B[0m";
            case '1' -> "\u001B[31m 1 \u001B[0m";
            case '2' -> "\u001B[32m 2 \u001B[0m";
            case '3' -> "\u001B[34m 3 \u001B[0m";
            case '4' -> "\u001B[33m 4 \u001B[0m";
            case '5' -> " 5 ";
            case '6' -> " 6 ";
            case '7' -> " 7 ";
            case '8' -> " 8 ";
            case 'x' -> "\u001B[40m x \u001B[0m";
            case 'i' -> " i ";
            case 'c' -> " c ";
            case 'b' -> " b ";
            default -> " ? ";
        };
    }

    public int toPlayerIndex() {
        return switch (character) {
            case '1' -> 0;
            case '2' -> 1;
            case '3' -> 2;
            case '4' -> 3;
            case '5' -> 4;
            case '6' -> 5;
            case '7' -> 6;
            case '8' -> 7;
            default -> -1;
        };
    }

    public String toString() {
        return toString(false);
    }

    /**
     * Returns all values for Players in ascending order.
     */
    public static TileValue[] getAllPlayerValues() {
        return new TileValue[]{PLAYER1, PLAYER2, PLAYER3, PLAYER4, PLAYER5, PLAYER6, PLAYER7, PLAYER8};
    }

    /**
     * Returns all values a player is allowed to expand to.
     * TODO: Performance?
     */
    public static List<TileValue> getAllFriendlyValues() {
        return Arrays.asList(EMPTY, BONUS, CHOICE, INVERSION);
    }
}
