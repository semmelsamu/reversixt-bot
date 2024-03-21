package map;

public enum TileType {
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

    public final char character;

    TileType(char character) {
        this.character = character;
    }

    public static boolean isPlayer(char c) {
        for (TileType type : TileType.values()) {
            if (type.character == c && (type == PLAYER1 || type == PLAYER2 || type == PLAYER3 ||
                    type == PLAYER4 || type == PLAYER5 || type == PLAYER6 ||
                    type == PLAYER7 || type == PLAYER8)) {
                return true;
            }
        }
        return false;
    }

    public static TileType fromChar(char c) {
        for (TileType type : TileType.values()) {
            if (type.character == c) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown character: " + c);
    }

    @Override
    public String toString() {
        return switch (character) {
            case '0' -> ". ";
            case '-' -> "##";
            case '1' -> "P1";
            case '2' -> "P2";
            case '3' -> "P3";
            case '4' -> "P4";
            case '5' -> "P5";
            case '6' -> "P6";
            case '7' -> "P7";
            case '8' -> "P8";
            case 'x' -> "x ";
            case 'i' -> "i ";
            case 'c' -> "c ";
            case 'b' -> "b ";
            default -> "??";
        };
    }
}
