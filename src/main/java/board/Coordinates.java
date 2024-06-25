package board;

/**
 * <ul>
 *     <li>X is the horizontal axis</li>
 *     <li>Y is the vertical axis</li>
 *     <li>Coordinates start at 0</li>
 *     <li>(0/0) is on the top left</li>
 * </ul>
 */
public class Coordinates {

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Attributes
    |
    |-----------------------------------------------------------------------------------------------
    */

    public final byte x;
    public final byte y;

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Constructor
    |
    |-----------------------------------------------------------------------------------------------
    */

    public Coordinates(int x, int y) {
        this.x = (byte) x;
        this.y = (byte) y;
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Methods
    |
    |-----------------------------------------------------------------------------------------------
    */

    public Coordinates inDirection(Direction direction) {
        return switch (direction) {
            case NORTH -> new Coordinates(x, y - 1);
            case NORTHEAST -> new Coordinates(x + 1, y - 1);
            case EAST -> new Coordinates(x + 1, y);
            case SOUTHEAST -> new Coordinates(x + 1, y + 1);
            case SOUTH -> new Coordinates(x, y + 1);
            case SOUTHWEST -> new Coordinates(x - 1, y + 1);
            case WEST -> new Coordinates(x - 1, y);
            case NORTHWEST -> new Coordinates(x - 1, y - 1);
        };
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Coordinates other = (Coordinates) obj;
        return x == other.x && y == other.y;
    }

    @Override
    public int hashCode() {
        return 100 * x + y;
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   To String
    |
    |-----------------------------------------------------------------------------------------------
    */

    @Override
    public String toString() {
        return "(" + x + "/" + y + ')';
    }

}
