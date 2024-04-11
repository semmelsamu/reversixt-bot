package game.evaluation;

import board.Coordinates;

public record TileRating(
        Coordinates coordinates,
        int value
) {

}
