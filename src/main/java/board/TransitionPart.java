package board;

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
}
