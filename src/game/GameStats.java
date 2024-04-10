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

        Logger.get().debug(());
    }

    public List<Coordinates> getAllCoordinatesWhereTileIs(Tile tile) {
        return tilesWithValue.get(tile);
    }




}
