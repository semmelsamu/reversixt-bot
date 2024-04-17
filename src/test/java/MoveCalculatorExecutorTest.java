import board.Coordinates;
import board.Tile;
import game.Game;
import game.GameFactory;
import game.MoveExecutor;
import org.junit.jupiter.api.Test;
import player.move.Move;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MoveCalculatorExecutorTest {

    // This makes sense! See constructor.
    Map<String, Map<Move, Map<Coordinates, Tile>>> testExpectations;

    public MoveCalculatorExecutorTest() {

        testExpectations = new HashMap<>();

        // @formatter:off

        // Expected values for boeseMap01
        testExpectations.put("maps/boeseMaps/boeseMap01.map", Map.of(
                // This is a valid move for this map
                new Move(Tile.PLAYER1, new Coordinates(7, 1)),
                Map.of(
                        // After executing the move above,
                        // On this position there should now be this tile:
                        new Coordinates(6, 1), Tile.PLAYER1,
                        new Coordinates(8, 1), Tile.PLAYER1
                )
        ));

        // @formatter:on

    }

    @Test
    public void testAll() {

        for (var testExpectation : testExpectations.entrySet()) {

            Game game = GameFactory.createFromFile(testExpectation.getKey());

            for (var validMove : testExpectation.getValue().entrySet()) {

                MoveExecutor moveExecutor = new MoveExecutor(game);
                moveExecutor.executeMove(validMove.getKey());

                for (var expectedValue : validMove.getValue().entrySet()) {
                    assertEquals(expectedValue.getValue(), game.getTile(expectedValue.getKey()));
                }

            }

        }

    }

}
