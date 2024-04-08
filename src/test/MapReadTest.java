package test;

import game.Game;
import game.GameFactory;
import util.File;
import util.Logger;

public class MapReadTest {

    public static int test() {

        int failedTests = 0;

        for(String map : File.getAllMaps()) {
            failedTests += testMap(map);
        }

        return failedTests;
    }

    public static int testMap(String filename) {
        try {
            Game game = GameFactory.createFromFile(filename);
            Logger.get().log(filename);
            return 0;
        }
        catch(Exception e) {
            Logger.get().error(filename + " generated error:" + e.getMessage());
            return 1;
        }
    }
}
