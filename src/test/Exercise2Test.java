package test;

import game.Game;
import util.Logger;

public class Exercise2Test {

    public static int test() {

        int failedTests = 0;

        for(String map : MapReadTest.maps) {
            String currentMap = "maps/" + map;

            Logger.log("Testing all moves for map " + map, 5);
            Game game = Game.createFromFile(currentMap);

            var moves = game.getValidMovesForCurrentPlayer();

            for(var move : moves) {
                Game testCase = Game.createFromFile(currentMap);
                Logger.log(testCase.toString(), 5);
                testCase.executeMove(move);
                Logger.log(testCase.toString(), 5);
            }
        }

        return failedTests;
    }
}
