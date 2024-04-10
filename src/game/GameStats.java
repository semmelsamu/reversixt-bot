package game;

import board.Coordinates;
import board.Tile;
import util.Logger;

import java.util.*;

public class GameStats {

    Map<Tile, List<Coordinates>> tilesWithValue;

    public GameStats(Game game) {
        tilesWithValue = new HashMap<>();
        for(Tile tile : Tile.values()) {
            tilesWithValue.put(tile, new LinkedList<>(game.getAllCoordinatesWhereTileIs(tile)));
        }
    }

    public List<Coordinates> getAllCoordinatesWhereTileIs(Tile tile) {
        return tilesWithValue.get(tile);
    }

    public void replaceTileAtCoordinates(Coordinates coordinates, Tile tile) {
        tilesWithValue.get(findKeyByValue(tilesWithValue, coordinates)).remove(coordinates);
        tilesWithValue.get(tile).add(coordinates);
    }

    // TODO: Performance
    public static <K, V> K findKeyByValue(Map<K, List<V>> map, V gesuchtesElement) {
        for (Map.Entry<K, List<V>> eintrag : map.entrySet()) {
            if (eintrag.getValue().contains(gesuchtesElement)) {
                return eintrag.getKey(); // Der Key wird zurückgegeben, wenn das Element gefunden wurde
            }
        }
        return null; // null wird zurückgegeben, wenn das Element nicht gefunden wurde
    }
}
