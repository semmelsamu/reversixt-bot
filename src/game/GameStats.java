package game;

import board.Tile;
import board.TileValue;
import util.Logger;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GameStats {

    Map<TileValue, List<Tile>> tilesWithValue;

    public GameStats(Game game) {

        tilesWithValue = new HashMap<>();

        for(TileValue value : TileValue.values()) {
            tilesWithValue.put(value, new LinkedList<>(game.getAllTilesWithValue(value)));
        }

        Logger.get().debug(tilesWithValue.toString());
    }

}
