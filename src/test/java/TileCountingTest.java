import board.Tile;
import game.Game;
import game.GameFactory;
import org.junit.jupiter.api.Test;
import util.TestLogger;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TileCountingTest {

    @Test
    public void testTileCount() {
        Map<String, Map<Tile, Integer>> mapsAndTileCounts = new HashMap<>();

        String windowMap = "maps/initialMaps/window.map";
        mapsAndTileCounts.put(windowMap, createTileCountMap(4));
        String checkerboardMap = "maps/initialMaps/checkerboard.map";
        mapsAndTileCounts.put(checkerboardMap, createTileCountMap(8));

        int errors = 0;

        for (var mapAndTileCounts : mapsAndTileCounts.entrySet()) {
            String mapPath =
                    mapAndTileCounts.getKey().equals(windowMap) ? windowMap : checkerboardMap;
            Game game = GameFactory.createFromFile(mapPath);

            for (var tileAndCount : mapAndTileCounts.getValue().entrySet()) {
                int calculatedTileCount =
                        game.getAllCoordinatesWhereTileIs(tileAndCount.getKey()).size();
                int expectedTileCount = tileAndCount.getValue();

                String message =
                        "Map " + mapAndTileCounts.getKey() + " calculated " + calculatedTileCount +
                                " tiles for player " + tileAndCount.getKey().character +
                                " (expected " + expectedTileCount + ")";

                if (calculatedTileCount != expectedTileCount) {
                    TestLogger.get().error(message);
                    errors++;
                }
            }
        }

        assertEquals(0, errors, "There were " + errors + " errors in tile count.");
    }

    private Map<Tile, Integer> createTileCountMap(int count) {
        Map<Tile, Integer> tileCountMap = new HashMap<>();
        tileCountMap.put(Tile.PLAYER1, count);
        tileCountMap.put(Tile.PLAYER2, count);
        tileCountMap.put(Tile.PLAYER3, count);
        tileCountMap.put(Tile.PLAYER4, count);
        return tileCountMap;
    }
}
