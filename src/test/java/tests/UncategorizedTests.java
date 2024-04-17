package tests;

import board.Coordinates;
import board.Tile;
import game.Game;
import game.GameFactory;
import game.MoveExecutor;
import org.junit.jupiter.api.Test;
import player.move.InversionMove;
import util.Logger;

public class UncategorizedTests {

    @Test
    public void test1() {
        Game game = GameFactory.createFromFile("maps/boeseMaps/boeseMap10.map");

        Logger.get().log(game.toString());

        (new MoveExecutor(game)).executeMove(
                new InversionMove(Tile.PLAYER1, new Coordinates(4, 4)));

        Logger.get().log(game.toString());
    }

}
