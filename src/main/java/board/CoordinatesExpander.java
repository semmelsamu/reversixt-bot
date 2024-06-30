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

        Set<Coordinates> result = new HashSet<>(coordinates);

        for (int i = 0; i < radius; i++) {

            Set<Coordinates> expandedCoordinates = new HashSet<>();

            for (Coordinates c : coordinates) {

                // Expand in every direction
                for (Direction direction : Direction.values()) {
                    TileReader tileReader = new TileReader(game, c, direction);

                    // This Direction has a dead end
                    if (!tileReader.hasNext()) {
                        continue;
                    }

                    tileReader.next();

                    // The expanded Tile is already present in the result
                    if (result.contains(tileReader.getCoordinates())) {
                        continue;
                    }

                    expandedCoordinates.add(tileReader.getCoordinates());
                }

                // No more Coordinates can be added
                if (expandedCoordinates.isEmpty()) {
                    break;
                }
            }

            result.addAll(expandedCoordinates);
            coordinates = new HashSet<>(expandedCoordinates);

        }

        return result;
    }

}
