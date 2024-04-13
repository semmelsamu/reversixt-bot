package game.evaluation;

import board.Coordinates;

public record MapTileRating(
        Coordinates coordinates,
        int value
) {

}
