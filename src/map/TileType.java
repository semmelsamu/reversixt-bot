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
