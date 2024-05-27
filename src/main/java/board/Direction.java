package board;

public enum Direction {

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Values
    |
    |-----------------------------------------------------------------------------------------------
    */

    NORTH(0),
    NORTHEAST(1),
    EAST(2),
    SOUTHEAST(3),
    SOUTH(4),
    SOUTHWEST(5),
    WEST(6),
    NORTHWEST(7);

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Attributes
    |
    |-----------------------------------------------------------------------------------------------
    */

    private final int value;

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Constructor
    |
    |-----------------------------------------------------------------------------------------------
    */

    Direction(int value) {
        this.value = value;
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Factory
    |
    |-----------------------------------------------------------------------------------------------
    */

    public static Direction fromValue(int value) {
        if(value >= Direction.values().length) {
            throw new IllegalArgumentException("Not valid direction: " + value);
        }
        return Direction.values()[value];
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Getter
    |
    |-----------------------------------------------------------------------------------------------
    */

    public int getValue() {
        return value;
    }

    /**
     * @return oppositeDirection
     */
    public Direction getOppositeDirection() {
        // taking 4 steps in overall 8 directions -> opposite direction
        int newIndex = (this.value + 4) % 8;
        return Direction.values()[newIndex];
    }
}
