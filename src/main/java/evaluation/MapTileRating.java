package evaluation;

import board.Coordinates;

/**
 * Container which contains coordinates and values. Values are added to the player rating.
 * Coordinates can be used to represent a board structure
 * TODO: may be unnecessary, if we don't need a map
 *
 * @param coordinates
 * @param value
 */
public record MapTileRating(
        Coordinates coordinates,
        int value
) {

}
