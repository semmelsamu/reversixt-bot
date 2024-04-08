package test;

import board.Coordinates;
import game.Game;
import game.GameFactory;
import game.MoveExecutor;
import player.move.Move;
import util.File;
import util.TestLogger;

public class Exercise2Test {

    public static int test() {

        int fails = 0;

        fails += testExceptions();
        fails += test1();

        return fails;
    }

    public static int testExceptions() {
        int failedTests = 0;

        for(String map : File.getAllMaps()) {

            Game game = GameFactory.createFromFile(map);
            TestLogger.get().debug(game.getBoard().toString());

            var moves = game.getValidMovesForCurrentPlayer();

            for(var move : moves) {
                Game testCase = GameFactory.createFromFile(map);
                MoveExecutor moveExecutor = new MoveExecutor(testCase);
                try {
                    moveExecutor.executeMove(move);
                    TestLogger.get().log("Map " + map + ", move " + move);
                    TestLogger.get().verbose(testCase.getBoard().toString());
                }
                catch (Exception e) {
                    TestLogger.get().error("Move execution failed: " + e.getMessage());
                    TestLogger.get().error("Initial map: " + game.toString());
                    TestLogger.get().error("Tried to execute move " + move);
                    TestLogger.get().error("Game: " + testCase.toString());
                    failedTests++;
                }
            }
        }

        return failedTests;
    }

    public static int test1() {
        TestLogger.get().debug("Test 01");
        Game game = GameFactory.createFromFile("maps/boeseMaps/boeseMap01.map");
        TestLogger.get().debug(game.toString());
        Move move = new Move(game.getPlayers()[0], game.getBoard().getTile(new Coordinates(7, 1)));
        MoveExecutor moveExecutor = new MoveExecutor(game);
        moveExecutor.executeMove(move);
        TestLogger.get().debug(game.toString());
        return 0;
    }
}
