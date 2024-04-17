package board;

import game.Game;

import java.util.Optional;

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

    /**
     * Return the current coordinates
     */
    public Coordinates getCoordinates() {
        return coordinates;
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
            return game.getTransitions().keySet().stream().anyMatch(
                    transitionPart -> transitionPart.coordinates().equals(coordinates) &&
                            transitionPart.direction().equals(direction));
        }

    }

    /**
     * Move the reader one in the current direction.
     *
     * @throws RuntimeException if there is no next tile
     */
    public void next() {

        Coordinates newCoordinates = coordinates.inDirection(direction);

        if (game.getTile(newCoordinates) != null && game.getTile(newCoordinates) != Tile.WALL) {
            // Regular neighbour
            coordinates = newCoordinates;
        } else {
            // Could be a transition
            Optional<TransitionPart> transition = game.getTransitions().keySet().stream()
                    .filter(transitionPart -> transitionPart.coordinates().equals(coordinates) &&
                            transitionPart.direction().equals(direction)).findAny();
            if (transition.isPresent()) {
                // It's a transition
                TransitionPart incomingTransition = game.getTransitions().get(transition.get());
                coordinates = incomingTransition.coordinates();
                direction = incomingTransition.direction();
            } else {
                throw new RuntimeException(
                        "Called next() on TileReader, but there is no next Tile");
            }
        }

    }
}
