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

        Logger.get().debug(tilesWithValueToString());
    }

    public List<Coordinates> getAllCoordinatesWhereTileIs(Tile tile) {
        return tilesWithValue.get(tile);
    }

    public void removeTile(Tile tile) {
        tilesWithValue.get(tile.getValue()).remove(tile);
    }

    public void addTile(Tile tile) {
        tilesWithValue.get(tile.getValue()).add(tile);
    }

    public String tilesWithValueToString() {
        StringBuilder result = new StringBuilder("Tiles\n");

        for(var key : tilesWithValue.keySet().stream().sorted().toArray()) {
            result.append(key.toString()).append(": ");
            for(var value : tilesWithValue.get(key)) {
                result.append(value.getPosition()).append(", ");
            }
            result.append("\n");
        }

        return result.toString();
    }

}
