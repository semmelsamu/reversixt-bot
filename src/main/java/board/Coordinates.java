package board;

import java.util.Objects;

/**
 * y is the vertical axis, x the horizontal axis.
 * Coordinates start at 0.
 * (0/0) is on the top left.
 */
public class Coordinates {

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Attributes
    |
    |-----------------------------------------------------------------------------------------------
    */

    public final int x;
    public final int y;

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Constructor
    |
    |-----------------------------------------------------------------------------------------------
    */

    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
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
        return Objects.hash(x, y);
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
