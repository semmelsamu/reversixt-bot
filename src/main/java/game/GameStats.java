package game;

import board.Coordinates;
import board.Tile;
import util.Collection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GameStats implements Cloneable{

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Attributes
    |
    |-----------------------------------------------------------------------------------------------
    */

    Map<Tile, Set<Coordinates>> coordinatesGroupedByTile;

    public GameStats(Game game) {
        coordinatesGroupedByTile = new HashMap<>();
        for (Tile tile : Tile.values()) {
            coordinatesGroupedByTile.put(tile,
                    new HashSet<>(game.getAllCoordinatesWhereTileIs(tile)));
        }
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Getters
    |
    |-----------------------------------------------------------------------------------------------
    */

    public Set<Coordinates> getAllCoordinatesWhereTileIs(Tile tile) {
        return coordinatesGroupedByTile.get(tile);
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Other Methods
    |
    |-----------------------------------------------------------------------------------------------
    */

    public void replaceTileAtCoordinates(Coordinates coordinates, Tile tile) {
        coordinatesGroupedByTile.get(
                        Collection.findKeyByValue(coordinatesGroupedByTile, coordinates))
                .remove(coordinates);
        coordinatesGroupedByTile.get(tile).add(coordinates);
    }

    @Override
    public GameStats clone() {
        try {
            GameStats clone = (GameStats) super.clone();
            clone.coordinatesGroupedByTile = new HashMap<>();
            for (Map.Entry<Tile, Set<Coordinates>> entry : coordinatesGroupedByTile.entrySet()) {
                clone.coordinatesGroupedByTile.put(entry.getKey(), new HashSet<>(entry.getValue()));
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
