package board;

import java.util.Objects;

public record TransitionPart(
        Coordinates coordinates,
        Direction direction
) implements Cloneable {

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
