import game.Game;
import game.GameFactory;
import org.junit.jupiter.api.Test;
import util.File;
import util.TestLogger;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class MapReadingTest {

    @Test
    public void testAllMaps() {
        for (String map : File.getAllMaps()) {
            assertEquals(testMap(map), 0);
        }
    }

    public int testMap(String filename) {
        try {
            Game game = GameFactory.createFromFile(filename);
            TestLogger.get().log("Map " + filename);
            return 0;
        } catch (Exception e) {
            TestLogger.get().error(filename + " generated error:" + e.getMessage());
            return 1;
        }
    }
}
