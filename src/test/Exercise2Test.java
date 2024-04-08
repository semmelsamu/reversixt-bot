package test;

import board.Coordinates;
import game.Game;
import game.GameFactory;
import game.MoveExecutor;
import player.move.InversionMove;
import player.move.Move;
import util.File;
import util.TestLogger;
import java.util.Arrays;

public class Exercise2Test {

    public static int test() {

        int fails = 0;

        fails += testExceptions();
        //fails += test1();
        fails += test2();

        return fails;
    }

    public static int testExceptions() {
        int failedTests = 0;

        for(String map : File.getAllMaps()) {

            Game game = GameFactory.createFromFile(map);

            TestLogger.get().log("Map " + map);
            TestLogger.get().verbose(game.toString());

            for(var move : game.getValidMovesForCurrentPlayer()) {

                Game testCase = GameFactory.createFromFile(map);

                try {
                    MoveExecutor moveExecutor = new MoveExecutor(testCase);
                    moveExecutor.executeMove(move);
                    TestLogger.get().log("Move " + move);
                    TestLogger.get().verbose(testCase.toString());
                }

                catch (Exception e) {
                    TestLogger.get().error("Move execution failed: " + e.getMessage() + " at " + Arrays.toString(e.getStackTrace()));
                    TestLogger.get().error("Initial map: " + game);
                    TestLogger.get().error("Tried to execute move " + move);
                    TestLogger.get().error("Game after attempting to execute move: " + testCase);
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
        Move move = new Move(game.getPlayers()[0], game.getTile(new Coordinates(7, 1)));
        MoveExecutor moveExecutor = new MoveExecutor(game);
        moveExecutor.executeMove(move);
        TestLogger.get().debug(game.toString());
        return 0;
    }

    public static int test2() {
        TestLogger.get().debug("Test 02");
        Game game = GameFactory.createFromFile("maps/boeseMaps/boeseMap10.map");
        InversionMove move = new InversionMove(game.getPlayers()[0], game.getTile(new Coordinates(4, 4)));
        MoveExecutor moveExecutor = new MoveExecutor(game);
        moveExecutor.executeMove(move);
        return 0;
    }
}
