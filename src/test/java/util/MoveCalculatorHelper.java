package util;

import board.Coordinates;
import board.Tile;
import game.Game;
import game.MoveCalculator;
import player.move.Move;

import java.util.Set;

public class MoveCalculatorHelper {

    public static Set<Move> getAllValidMovesForPlayer(Game game, Tile player){
        return new MoveCalculator(game).getValidMovesForPlayer(player);
    }

    public static Move createDummyMove(Tile player, int x, int y){
        return new Move(player, new Coordinates(x, y));
    }
}
