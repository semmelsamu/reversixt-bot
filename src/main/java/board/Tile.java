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
     * States that this tile has not been occupied by any player
     */
    public boolean isUnoccupied() {
        return (this == EMPTY || this == INVERSION || this == BONUS || this == CHOICE);
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
    public static Tile getTileForPlayerNumber(int player) {
        if(player  > 8){
            throw new IllegalArgumentException("Player number is higher than 8");
        }
        return Tile.values()[player];
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

    public byte toByte() {
        return switch (character) {
            case '1' -> 0x1;
            case '2' -> 0x2;
            case '3' -> 0x3;
            case '4' -> 0x4;
            case '5' -> 0x5;
            case '6' -> 0x6;
            case '7' -> 0x7;
            case '8' -> 0x8;
            case '0' -> 0x9;
            case '-' -> 0xa;
            case 'b' -> 0xb;
            case 'c' -> 0xc;
            case 'i' -> 0xd;
            case 'x' -> 0xe;
            default -> throw new IllegalStateException("Unexpected value: " + character);
        };
    }

    public static Tile fromByte(byte b) {
        return switch (b) {
            case 0x1 -> PLAYER1;
            case 0x2 -> PLAYER2;
            case 0x3 -> PLAYER3;
            case 0x4 -> PLAYER4;
            case 0x5 -> PLAYER5;
            case 0x6 -> PLAYER6;
            case 0x7 -> PLAYER7;
            case 0x8 -> PLAYER8;
            case 0x9 -> EMPTY;
            case 0xa -> WALL;
            case 0xb -> BONUS;
            case 0xc -> CHOICE;
            case 0xd -> INVERSION;
            case 0xe -> EXPANSION;
            default -> throw new IllegalStateException("Unexpected value: " + b);
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
