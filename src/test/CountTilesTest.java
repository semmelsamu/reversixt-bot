package test;

import board.TileValue;
import game.Game;
import util.Logger;

import java.util.HashMap;
import java.util.Map;

public class CountTilesTest {
    public static int countTiles() {

        Map<String, Map<TileValue, Integer>> mapsAndTileCounts = new HashMap<>();

        mapsAndTileCounts.put("initialMaps/window.map", new HashMap<>());
        mapsAndTileCounts.get("initialMaps/window.map").put(TileValue.PLAYER1, 4);
        mapsAndTileCounts.get("initialMaps/window.map").put(TileValue.PLAYER2, 4);
        mapsAndTileCounts.get("initialMaps/window.map").put(TileValue.PLAYER3, 4);
        mapsAndTileCounts.get("initialMaps/window.map").put(TileValue.PLAYER4, 4);

        mapsAndTileCounts.put("initialMaps/checkerboard.map", new HashMap<>());
        mapsAndTileCounts.get("initialMaps/checkerboard.map").put(TileValue.PLAYER1, 8);
        mapsAndTileCounts.get("initialMaps/checkerboard.map").put(TileValue.PLAYER2, 8);
        mapsAndTileCounts.get("initialMaps/checkerboard.map").put(TileValue.PLAYER3, 8);
        mapsAndTileCounts.get("initialMaps/checkerboard.map").put(TileValue.PLAYER4, 8);
        mapsAndTileCounts.get("initialMaps/checkerboard.map").put(TileValue.PLAYER5, 8);
        mapsAndTileCounts.get("initialMaps/checkerboard.map").put(TileValue.PLAYER6, 8);
        mapsAndTileCounts.get("initialMaps/checkerboard.map").put(TileValue.PLAYER7, 8);
        mapsAndTileCounts.get("initialMaps/checkerboard.map").put(TileValue.PLAYER8, 8);

        int errors = 0;

        for(var mapAndTileCounts : mapsAndTileCounts.entrySet()) {

            Game game = Game.createFromFile("maps/" + mapAndTileCounts.getKey());

            for(var tileAndCount : mapAndTileCounts.getValue().entrySet()) {
                int calculatedTileCount = game.getBoard().getAllTilesWithValue(tileAndCount.getKey()).size();
                int expectedTileCount = tileAndCount.getValue();

                String message = mapAndTileCounts.getKey()
                        + " calculated "
                        + calculatedTileCount
                        + " tiles for player "
                        + tileAndCount.getKey().character
                        + " (expected "
                        + expectedTileCount
                        + ")";

                if(calculatedTileCount == expectedTileCount) {
                    Logger.log(message, 5);
                }
                else {
                    Logger.error(message, 5);
                    errors++;
                }
            }
        }

        return errors;
    }
}
