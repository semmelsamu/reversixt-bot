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
            Logger.get().log("Testing all moves for map " + map);

            Game game = GameFactory.createFromFile(map);
            Logger.get().debug(game.getBoard().toString());

            var moves = game.getValidMovesForCurrentPlayer();

            for(var move : moves) {
                Game testCase = GameFactory.createFromFile(map);
                MoveExecutor moveExecutor = new MoveExecutor(testCase);
                try {
                    moveExecutor.executeMove(move);
                    Logger.get().log("Successfully executed move");
                    Logger.get().verbose(testCase.getBoard().toString());
                }
                catch (Exception e) {
                    Logger.get().error("Move execution failed: " + e.getMessage());
                    Logger.get().error("Initial map: " + game.toString());
                    Logger.get().error("Tried to execute move " + move);
                    Logger.get().error("Game: " + testCase.toString());
                    failedTests++;
                }
            }
        }

        return failedTests;
    }

    public static int test1() {
        Game game = GameFactory.createFromFile("maps/boeseMaps/boeseMap10.map");
        Logger.get().log(game.toString());
        return 0;
    }
}
