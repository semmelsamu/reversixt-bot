package board;

/**
 * y is the vertical axis, x the horizontal axis.
 * Coordinates start at 0.
 * (0/0) is on the top left.
 */
public class Coordinates {
    public final int x;
    public final int y;

    /*
    |--------------------------------------------------------------------------
    | Constructor
    |--------------------------------------------------------------------------
    */

    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /*
    |--------------------------------------------------------------------------
    | Magic functions
    |--------------------------------------------------------------------------
    */

    @Override
    public String toString() {
        return "(" + x + "/" + y + ')';
    }

}
