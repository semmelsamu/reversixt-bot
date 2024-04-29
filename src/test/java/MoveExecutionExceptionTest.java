import board.Tile;
import game.Game;
import game.GameFactory;
import game.MoveCalculator;
import org.junit.jupiter.api.Test;
import player.move.Move;
import util.File;
import util.Logger;
import util.MoveExecutorHelper;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class MoveExecutionExceptionTest {

    @Test
    public void everyFirstMoveOnEveryMap_noException_test() {
        Logger.defaultPriority = 3;
        for (String map : File.getAllMaps()) {
            Game game = GameFactory.createFromFile(map);

            for (Move move : (new MoveCalculator(game).getValidMovesForPlayer(1))) {
                Game testCase = GameFactory.createFromFile(map);

                assertDoesNotThrow(
                        () -> MoveExecutorHelper.executeExistingMovePlayer1(testCase, move));

            }
        }
    }
}
