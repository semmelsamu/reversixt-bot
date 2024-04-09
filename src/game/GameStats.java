package game;

import board.Coordinates;
import board.Tile;
import board.TileValue;
import util.Logger;

import java.util.*;

public class GameStats {

    Map<TileValue, List<Tile>> tilesWithValue;

    public GameStats(Game game) {

        tilesWithValue = new HashMap<>();

        for(TileValue value : TileValue.values()) {
            tilesWithValue.put(value, new LinkedList<>(game.getAllTilesWithValue(value)));
        }

        Logger.get().debug(tilesWithValueToString());
    }

    public void removeTile(Coordinates position) {

    }

    public void addTile(Tile tile) {

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
