package test;

import game.Game;
import game.GameFactory;
import game.MoveExecutor;
import util.File;
import util.Logger;

public class Exercise2Test {

    public static int test() {
        return testExceptions();
    }

    public static int testExceptions() {
        int failedTests = 0;

        for(String map : File.getAllMaps()) {
            Logger.log("Testing all moves for map " + map, 5);

            Game game = GameFactory.createFromFile(map);
            Logger.debug(game.getBoard().toString(), 5);

            var moves = game.getValidMovesForCurrentPlayer();

            for(var move : moves) {
                Game testCase = GameFactory.createFromFile(map);
                MoveExecutor moveExecutor = new MoveExecutor(testCase);
                try {
                    moveExecutor.executeMove(move);
                    Logger.log("Successfully executed move", 5);
                    Logger.verbose(testCase.getBoard().toString(), 5);
                }
                catch (Exception e) {
                    Logger.error("Move execution failed: " + e.getMessage(), 5);
                    Logger.error("Initial map: " + game.toString(), 5);
                    Logger.error("Tried to execute move " + move, 5);
                    Logger.error("Game: " + testCase.toString(), 5);
                    failedTests++;
                }
            }
        }

        return failedTests;
    }

    public static int test1() {
        Game game = GameFactory.createFromFile("maps/boeseMaps/boeseMap10.map");
        Logger.log(game.toString(), 5);
        return 0;
    }
}
