package map;

public record Transition(Direction outgoingDirection, Tile target, Direction ingoingDirection) {
}
