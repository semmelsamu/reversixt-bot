package board;

/**
 * Formerly TileValue formerly TileType
 */
public enum Tile {


    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Values
    |
    |-----------------------------------------------------------------------------------------------
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
    |-----------------------------------------------------------------------------------------------
    |
    |   Attributes
    |
    |-----------------------------------------------------------------------------------------------
    */

    /**
     * The value of the tile, stored as a character.
     */
    public final char character;

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Factory
    |
    |-----------------------------------------------------------------------------------------------
    */

    /**
     * Returns the enum from a character.
     */
    public static Tile fromChar(char c) {
        for (Tile type : Tile.values()) {
            if (type.character == c) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown character: " + c);
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Constructor
    |
    |-----------------------------------------------------------------------------------------------
    */

    /**
     * Creates a new enum from a character.
     */
    Tile(char character) {
        this.character = character;
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Information Functions
    |
    |-----------------------------------------------------------------------------------------------
    */

    /**
     * States if the tile is occubyable, i.e. a player
     */
    public boolean isOccupyable() {
        return this != WALL;
    }

    /**
     * States if the tile is occupied by a player.
     */
    public boolean isPlayer() {
        return (this == PLAYER1 || this == PLAYER2 || this == PLAYER3 || this == PLAYER4 ||
                this == PLAYER5 || this == PLAYER6 || this == PLAYER7 || this == PLAYER8);
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Value Functions
    |
    |-----------------------------------------------------------------------------------------------
    */

    /**
     * Returns all values for Players in ascending order.
     */
    public static Tile[] getAllPlayerValues() {
        return new Tile[]{PLAYER1, PLAYER2, PLAYER3, PLAYER4, PLAYER5, PLAYER6, PLAYER7, PLAYER8};
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Utility Functions
    |
    |-----------------------------------------------------------------------------------------------
    */

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

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   To String
    |
    |-----------------------------------------------------------------------------------------------
    */

    public String toString() {
        return toString(false);
    }

    public String toString(boolean useColors) {
        if (!useColors) {
            return String.valueOf(character);
        }

        return switch (character) {
            case '0' -> " . ";
            case '-' -> "\u001B[47m   \u001B[0m";
            case '1' -> "\u001B[31m 1 \u001B[0m";
            case '2' -> "\u001B[34m 2 \u001B[0m";
            case '3' -> "\u001B[32m 3 \u001B[0m";
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
}
