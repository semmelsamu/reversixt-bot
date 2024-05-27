package board;

import java.util.Objects;

public record TransitionPart(
        Coordinates coordinates,
        Direction direction
) implements Cloneable {

    public short toShort() {
        int direction = this.direction().getValue();
        int x = this.coordinates().x;
        int y = this.coordinates().y;

        // Combine the values into a short
        return (short) ((direction << 12) | (x << 6) | y);
    }

    public static TransitionPart fromShort(short s) {
        int directionValue = (s >> 12) & 0xF; // Extract the first 4 bits for the direction
        int x = (s >> 6) & 0x3F; // Extract the next 6 bits for the x coordinate
        int y = s & 0x3F; // Extract the last 6 bits for the y coordinate

        Direction direction = Direction.values()[directionValue];
        Coordinates coordinates = new Coordinates(x, y);

        return new TransitionPart(coordinates, direction);
    }

    @Override
    public TransitionPart clone() {
        try {
            return (TransitionPart) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TransitionPart that = (TransitionPart) o;
        return direction == that.direction && Objects.equals(coordinates, that.coordinates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coordinates, direction);
    }
}
