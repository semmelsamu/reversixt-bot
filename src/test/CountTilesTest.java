package test;

import board.TileValue;
import game.Game;
import game.GameFactory;
import util.TestLogger;

import java.util.HashMap;
import java.util.Map;

public class CountTilesTest {
    public static int test() {

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

            Game game = GameFactory.createFromFile("maps/" + mapAndTileCounts.getKey());

            for(var tileAndCount : mapAndTileCounts.getValue().entrySet()) {
                int calculatedTileCount = game.getAllTilesWithValue(tileAndCount.getKey()).size();
                int expectedTileCount = tileAndCount.getValue();

                String message = "Map" + mapAndTileCounts.getKey()
                        + " calculated "
                        + calculatedTileCount
                        + " tiles for player "
                        + tileAndCount.getKey().character
                        + " (expected "
                        + expectedTileCount
                        + ")";

                if(calculatedTileCount == expectedTileCount) {
                    TestLogger.get().log(message);
                }
                else {
                    TestLogger.get().error(message);
                    errors++;
                }
            }
        }

        return errors;
    }
}
