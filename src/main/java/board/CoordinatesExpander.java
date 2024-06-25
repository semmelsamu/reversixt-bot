package board;

import game.Game;

import java.util.HashSet;
import java.util.Set;

public class CoordinatesExpander {

    public static Set<Coordinates> expandCoordinates(Game game, Set<Coordinates> coordinates,
                                                     int radius) {

        // TODO: Recursion is slow! Also, make it only expand the Coordinates that were expanded
        //  the last time!

        if (radius == 0) {
            return coordinates;
        }

        Set<Coordinates> expandedCoordinates = new HashSet<>();

        for (Coordinates c : coordinates) {
            // Expand in every direction
            for (Direction direction : Direction.values()) {
                TileReader tileReader = new TileReader(game, c, direction);
                if (tileReader.hasNext()) {
                    tileReader.next();
                    expandedCoordinates.add(tileReader.getCoordinates());
                }
            }
        }

        Set<Coordinates> result = new HashSet<>(coordinates);
        result.addAll(expandedCoordinates);
        result.addAll(expandCoordinates(game, expandedCoordinates, radius - 1));

        return result;
    }

}
