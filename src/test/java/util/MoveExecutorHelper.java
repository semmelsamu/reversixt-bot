package util;

import board.Coordinates;
import board.Tile;
import game.Game;
import game.MoveExecutor;
import move.InversionMove;
import move.Move;

public class MoveExecutorHelper {

    public static void executeInversionMovePlayer1(Game game, int x, int y) {
        MoveExecutor.executeMove(game,
                new InversionMove(1, new Coordinates(x, y)));
    }

    public static void executeExistingMovePlayer1(Game game, Move move) {
        MoveExecutor.executeMove(game, move);
    }

    public static Tile getTile(Game game, int x, int y){
        return game.getTile(new Coordinates(x, y));
    }

}
