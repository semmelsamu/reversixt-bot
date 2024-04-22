package util;

import board.Coordinates;
import board.Tile;
import game.Game;
import game.MoveExecutor;
import player.move.InversionMove;

public class MoveExecutorHelper {

    public static void executeMovePlayer1(Game game, int x, int y) {
        (new MoveExecutor(game)).executeMove(
                new InversionMove(Tile.PLAYER1, new Coordinates(x, y)));
    }

}
