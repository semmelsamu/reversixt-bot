import game.Game;
import game.GameFactory;
import game.MoveExecutor;
import org.junit.jupiter.api.Test;
import util.File;
import util.TestLogger;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.fail;

public class MoveExecutionTest {

    @Test
    public void testEveryMove() {

        for (String map : File.getAllMaps()) {
            Game game = GameFactory.createFromFile(map);

            TestLogger.get().log("Map " + map);
            TestLogger.get().verbose(game.toString());

            for (var move : game.getValidMovesForCurrentPlayer()) {
                Game testCase = GameFactory.createFromFile(map);

                try {
                    MoveExecutor moveExecutor = new MoveExecutor(testCase);
                    moveExecutor.executeMove(move);
                    TestLogger.get().log("Move " + move);
                    TestLogger.get().verbose(testCase.toString());
                } catch (Exception e) {
                    TestLogger.get().error("Move execution failed: " + e.getMessage() + " at " +
                            Arrays.toString(e.getStackTrace()));
                    TestLogger.get().error("Initial map: " + game);
                    TestLogger.get().error("Tried to execute move " + move);
                    TestLogger.get().error("Game after attempting to execute move: " + testCase);
                    fail("Test failed");
                }
            }
        }
    }
}
