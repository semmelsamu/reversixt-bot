package map;

public record Transition(Direction outgoingDirection, MapTile target, Direction ingoingDirection) {
}
