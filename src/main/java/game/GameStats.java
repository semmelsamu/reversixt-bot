package game;

import board.Coordinates;
import board.Tile;
import util.FindKeyByValue;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GameStats {

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Attributes
    |
    |-----------------------------------------------------------------------------------------------
    */

    Map<Tile, List<Coordinates>> tilesWithValue;

    public GameStats(Game game) {
        tilesWithValue = new HashMap<>();
        for (Tile tile : Tile.values()) {
            tilesWithValue.put(tile, new LinkedList<>(game.getAllCoordinatesWhereTileIs(tile)));
        }
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Getters
    |
    |-----------------------------------------------------------------------------------------------
    */

    public List<Coordinates> getAllCoordinatesWhereTileIs(Tile tile) {
        return tilesWithValue.get(tile);
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Other Methods
    |
    |-----------------------------------------------------------------------------------------------
    */

    public void replaceTileAtCoordinates(Coordinates coordinates, Tile tile) {
        tilesWithValue.get(
                // TODO: Performance. FindKeyByValue is slow.
                FindKeyByValue.findKeyByValue(tilesWithValue, coordinates)).remove(coordinates);

        tilesWithValue.get(tile).add(coordinates);
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Helper Methods
    |
    |-----------------------------------------------------------------------------------------------
    */


}
