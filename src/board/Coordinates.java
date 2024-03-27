package board;

public class Coordinates {
    private final int x;
    private final int y;

    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "[" +
                "x=" + x + ", " +
                "y=" + y + ']';
    }

}
