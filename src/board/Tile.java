package board;

import util.Logger;

import java.util.Arrays;

public class Tile {

    /**
     * The contents/value (old: type) of the tile, e.g. empty, player, wall, ...
     */
    private TileValue value;

    private Neighbour[] neighbours;

    /**
     * The position of the tile on the board. Used for quick access.
     */
    private final Coordinates position;

    /*
    |--------------------------------------------------------------------------
    | Constructor
    |--------------------------------------------------------------------------
    */

    public Tile(TileValue type, Coordinates position) {
        this.value = type;
        this.position = position;
        this.neighbours = new Neighbour[8];
    }

    /*
    |--------------------------------------------------------------------------
    | Getters & Setters
    |--------------------------------------------------------------------------
    */

    public TileValue getValue() {
        return value;
    }

    public Neighbour getNeighbour(Direction direction) {
        return neighbours[direction.getValue()];
    }

    public void setNeighbour(Direction direction, Neighbour neighbour) {
        if (neighbours[direction.getValue()] != null) {
            Logger.get().warn("Reassigning neighbour is probably unwanted");
        }

        // Neighbours are only tiles which the player can expand to. See definition of neighbour record.
        if (!neighbour.tile().value.isExpandable()) {
            // Logger.verbose("Skipping not expandable neighbour");
            return;
        }

        neighbours[direction.getValue()] = neighbour;
    }

    public void setValue(TileValue value) {
        this.value = value;
    }

    /*
    |--------------------------------------------------------------------------
    | Magic functions
    |--------------------------------------------------------------------------
    */

    @Override
    public String toString() {
        return "MapTile{position=" + position + ", value=" + value + ", neighbours=" + Arrays.toString(neighbours) + "}";
    }

    public Coordinates getPosition() {
        return position;
    }
}