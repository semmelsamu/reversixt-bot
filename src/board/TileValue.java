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

    public final char character;

    public static TileValue fromChar(char c) {
        for (TileValue type : TileValue.values()) {
            if (type.character == c) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown character: " + c);
    }

    TileValue(char character) {
        this.character = character;
    }

    /*
    |--------------------------------------------------------------------------
    | Information functions
    |--------------------------------------------------------------------------
    */

    public boolean isExpandable() {
        return TileValue.fromChar(this.character) != WALL;
    }

    public boolean isPlayer() {
        TileValue value = TileValue.fromChar(this.character);
        return (value == PLAYER1 || value == PLAYER2 || value == PLAYER3 || value == PLAYER4 || value == PLAYER5 || value == PLAYER6);
    }

    /*
    |--------------------------------------------------------------------------
    | Utility
    |--------------------------------------------------------------------------
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
}
