package board;

/**
 * A neighbour is a tile that a player may expand to, so basically every tile except a wall (-).
 *
 * @param tile            The actual tile
 * @param directionChange (Optional) the new direction to expand to. See transitions.
 */
public record Neighbour(Tile tile, Direction directionChange) {
    public Neighbour(Tile tile) {
        this(tile, null);
    }

    @Override
    public String toString() {
        if (directionChange == null) return "Neighbour";
        return "Transition{directionChange=" + directionChange + "}";
    }
}
