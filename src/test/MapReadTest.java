package test;

import game.Game;
import util.Logger;

public class MapReadTest {

    public static int testAll() {

        String[] maps = new String[] {
                "initialMaps/checkerboard.map",
                "initialMaps/crown.map",
                "initialMaps/diamond.map",
                "initialMaps/example.map",
                "initialMaps/heart.map",
                "initialMaps/infinity.map",
                "initialMaps/scope.map",
                "initialMaps/window.map",

                "boeseMaps/boeseMap01.map",
                "boeseMaps/boeseMap02.map",
                "boeseMaps/boeseMap03.map",
                "boeseMaps/boeseMap04.map",
                "boeseMaps/boeseMap05.map",
                "boeseMaps/boeseMap06.map",
                "boeseMaps/boeseMap07.map",
                "boeseMaps/boeseMap08.map",
                "boeseMaps/boeseMap09.map",
                "boeseMaps/boeseMap10.map",
                "boeseMaps/boeseMap11.map",
        };

        int failedTests = 0;

        for(String map : maps) {
            failedTests += testMap("maps/" + map);
        }

        return failedTests;
    }

    public static int testMap(String filename) {
        try {
            Game game = Game.createFromFile(filename);
            Logger.ON = true;
            Logger.log("Map " + filename);
            Logger.ON = false;
            game.getBoard().print();
            return 0;
        }
        catch(Error e) {
            Logger.ON = true;
            Logger.error("Map " + filename + " failed");
            Logger.ON = false;
            return 1;
        }
    }
}
