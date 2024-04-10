package board;

import game.Game;

public class TileReader {

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Attributes
    |
    |-----------------------------------------------------------------------------------------------
    */

    /**
     * The game object this tile reader operates on.
     */
    private final Game game;

    /**
     * The coordinates of the current tile.
     */
    private Coordinates coordinates;

    /**
     * The direction the reader moves when next() is called.
     */
    private Direction direction;

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Constructor
    |
    |-----------------------------------------------------------------------------------------------
    */

    public TileReader(Game game, Coordinates coordinates, Direction direction) {
        this.game = game;
        this.coordinates = coordinates;
        this.direction = direction;
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Methods
    |
    |-----------------------------------------------------------------------------------------------
    */

    /**
     * Return the tile which the reader currently points at.
     */
    public Tile getTile() {
        return game.getTile(coordinates);
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    /**
     * Return if the reader has a neighbour in the current direction.
     */


    public boolean hasNext() {

        if (game.getTile(coordinates.inDirection(direction)) != null &&
                game.getTile(coordinates.inDirection(direction)) != Tile.WALL) {
            // Neighbour
            return true;
        } else {
            // Check transition
            return game.getTransitions().containsKey(new TransitionPart(coordinates, direction));
        }

    }

    /**
     * Move the reader one in the current direction.
     */
    public void next() {

        Coordinates newCoordinates = coordinates.inDirection(direction);

        if (game.getTile(newCoordinates) != null && game.getTile(newCoordinates) != Tile.WALL) {
            // Regular neighbour
            coordinates = newCoordinates;
        } else {
            // Could be a transition
            TransitionPart outgoingTransition = new TransitionPart(coordinates, direction);
            if (game.getTransitions().containsKey(outgoingTransition)) {
                // It's a transition
                TransitionPart incomingTransition = game.getTransitions().get(outgoingTransition);
                coordinates = incomingTransition.coordinates();
                direction = incomingTransition.direction();
            } else {
                throw new RuntimeException(
                        "Called next() on TileReader, but there is no next Tile");
            }
        }

    }
}
