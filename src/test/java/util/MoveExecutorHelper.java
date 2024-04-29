package util;

import board.Coordinates;
import board.Tile;
import game.Game;
import game.MoveExecutor;
import player.move.InversionMove;
import player.move.Move;

public class MoveExecutorHelper {

    public static void executeInversionMovePlayer1(Game game, int x, int y) {
        (new MoveExecutor(game)).executeMove(
                new InversionMove(game.getPlayer(1), new Coordinates(x, y)));
    }

    public static void executeExistingMovePlayer1(Game game, Move move) {
        (new MoveExecutor(game)).executeMove(move);
    }

    public static Tile getTile(Game game, int x, int y){
        return game.getTile(new Coordinates(x, y));
    }

}
