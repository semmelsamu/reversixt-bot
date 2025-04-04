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

    /**
     * How often next() was called
     */
    private int tileNumber;

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
        this.tileNumber = 0;
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

    /**
     * Return the current coordinates
     */
    public Coordinates getCoordinates() {
        return coordinates;
    }

    /**
     * Get how often next() was called
     */
    public int getTileNumber() {
        return tileNumber;
    }

    /**
     * Return if the reader has a neighbour in the current direction.
     */
    public boolean hasNext() {

        if (game.coordinatesLayInBoard(coordinates.inDirection(direction)) &&
                game.getTile(coordinates.inDirection(direction)) != Tile.WALL) {
            // Neighbour
            return true;
        } else {
            // Check transition
            return getTransitionCounterpart(new TransitionPart(coordinates, direction)) != null;
        }

    }

    /**
     * Move the reader one in the current direction.
     *
     * @throws RuntimeException if there is no next tile
     */
    public void next() {

        Coordinates newCoordinates = coordinates.inDirection(direction);

        if (game.coordinatesLayInBoard(newCoordinates) && game.getTile(newCoordinates) != Tile.WALL) {
            // Regular neighbour
            coordinates = newCoordinates;
        } else {
            // Could be a transition
            TransitionPart transitionCounterpart =
                    getTransitionCounterpart(new TransitionPart(coordinates, direction));
            if (transitionCounterpart != null) {
                coordinates = transitionCounterpart.coordinates();
                direction = transitionCounterpart.direction();
            } else {
                throw new RuntimeException(
                        "Called next() on TileReader, but there is no next Tile");
            }
        }

        tileNumber++;

    }

    /**
     * Gets the transition counterpart if it is not already bombed (occupied by a wall)
     */
    private TransitionPart getTransitionCounterpart(TransitionPart transitionPart) {
        // Is there a transition?
        // Not using contains because get has better run time
        if (game.getTransitions().get(transitionPart.toShort()) == null) {
            return null;
        }

        TransitionPart transitionCounterpart =
                TransitionPart.fromShort(game.getTransitions().get(transitionPart.toShort()));

        // Is the transition blocked?
        if (game.getTile(transitionCounterpart.coordinates()).equals(Tile.WALL)) {
            return null;
        }

        return transitionCounterpart;
    }
}
