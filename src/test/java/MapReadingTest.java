import game.GameFactory;
import org.junit.jupiter.api.Test;
import util.File;
import util.TestLogger;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


public class MapReadingTest {

    @Test
    public void createFromFile_test() {
        List<String> allMaps = File.getAllMaps();

        assertDoesNotThrow(() -> {
            for (String map : allMaps) {
                TestLogger.get().log("Map " + map);
                GameFactory.createFromFile(map);
            }
        });
    }

    @Test
    public void getAllMaps_test() {
        assertDoesNotThrow(File::getAllMaps);
    }
}
