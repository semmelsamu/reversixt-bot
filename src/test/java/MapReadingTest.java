import game.GameFactory;
import org.junit.jupiter.api.Test;
import util.File;
import util.Logger;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


public class MapReadingTest {

    Logger logger = new Logger(this.getClass().getName());

    @Test
    public void createFromFile_test() {
        List<String> allMaps = File.getAllMaps();

        assertDoesNotThrow(() -> {
            for (String map : allMaps) {
                logger.log("Map " + map);
                GameFactory.createFromFile(map);
            }
        });
    }

    @Test
    public void getAllMaps_test() {
        assertDoesNotThrow(File::getAllMaps);
    }
}
