import board.Coordinates;
import board.Tile;
import game.Game;
import game.GameFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import util.Logger;
import util.MoveExecutorHelper;

import java.util.Set;

public class InversionTileTest {

    @Test
    public void boeseMap10_test() {
        Game game = GameFactory.createFromFile("maps/boeseMaps/boeseMap10.map");

        Logger.get().log(game.toString());

        MoveExecutorHelper.executeInversionMovePlayer1(game, 4, 4);

        Logger.get().log(game.toString());

        Set<Coordinates> player1Tiles =
                game.getGameStats().getAllCoordinatesWhereTileIs(Tile.PLAYER1);
        Set<Coordinates> player3Tiles =
                game.getGameStats().getAllCoordinatesWhereTileIs(Tile.PLAYER3);
        Set<Coordinates> player2Tiles =
                game.getGameStats().getAllCoordinatesWhereTileIs(Tile.PLAYER2);
        Assertions.assertTrue(player1Tiles.isEmpty(), "No Player 1 tiles");
        Assertions.assertTrue(player3Tiles.isEmpty(), "No Player 3 tiles");
        Assertions.assertEquals(25, player2Tiles.size(), "Right amount of player 2 tiles");
    }

}
