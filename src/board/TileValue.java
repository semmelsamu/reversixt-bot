package board;

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

    /**
     * Prints the tile value in color to the system out.
     */
    public String print() {
        return switch (character) {
            case '0' -> ". ";
            case '-' -> "\u001B[47m##\u001B[0m";
            case '1' -> "\u001B[31mP1\u001B[0m";
            case '2' -> "\u001B[32mP2\u001B[0m";
            case '3' -> "\u001B[34mP3\u001B[0m";
            case '4' -> "\u001B[33mP4\u001B[0m";
            case '5' -> "P5";
            case '6' -> "P6";
            case '7' -> "P7";
            case '8' -> "P8";
            case 'x' -> "\u001B[40mx \u001B[0m";
            case 'i' -> "i ";
            case 'c' -> "c ";
            case 'b' -> "b ";
            default -> "??";
        };
    }

    /**
     *
     * @param initialPlayers Number of players participating in the game
     * @return Array of all PlayerValues
     * Especially needed when creating an Array of all players in Game Constructor
     */
    public static TileValue[] getPlayerValues(int initialPlayers){
        TileValue[] tileValues = TileValue.values();
        TileValue[] playerValues = new TileValue[initialPlayers];
        for (int i = 0; i < initialPlayers; i++) {
            playerValues[i] = tileValues[i + 1];
        }
        return playerValues;
    }
}
