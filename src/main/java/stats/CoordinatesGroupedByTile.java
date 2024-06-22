package stats;

import board.Coordinates;
import board.Tile;
import game.Game;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CoordinatesGroupedByTile {

    private Map<Tile, Set<Coordinates>> coordinatesGroupedByTile;

    public CoordinatesGroupedByTile(Game game) {

        coordinatesGroupedByTile = new HashMap<>();

        for (Tile tile : Tile.values()) {

            coordinatesGroupedByTile.put(tile, new HashSet<>());

            for (int y = 0; y < game.getHeight(); y++) {
                for (int x = 0; x < game.getWidth(); x++) {
                    if (game.getTile(new Coordinates(x, y)) == tile) {
                        coordinatesGroupedByTile.get(tile).add(new Coordinates(x, y));
                    }
                }
            }

        }

    }

    public Set<Coordinates> getAllCoordinatesWhereTileIs(Tile tile) {
        return coordinatesGroupedByTile.get(tile);
    }

    public void updateCoordinates(Coordinates coordinates, Tile oldValue, Tile newValue) {
        coordinatesGroupedByTile.get(oldValue).remove(coordinates);
        coordinatesGroupedByTile.get(newValue).add(coordinates);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {

        CoordinatesGroupedByTile clone = (CoordinatesGroupedByTile) super.clone();

        clone.coordinatesGroupedByTile = new HashMap<>();

        for (Map.Entry<Tile, Set<Coordinates>> entry : coordinatesGroupedByTile.entrySet()) {
            clone.coordinatesGroupedByTile.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }

        return clone;
    }
}
