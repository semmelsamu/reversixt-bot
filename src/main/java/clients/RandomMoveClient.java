package clients;

import board.Tile;
import game.Game;
import game.GameFactory;
import game.MoveCalculator;
import game.MoveExecutor;
import player.move.Move;
import util.Logger;
import util.SetUtils;

/**
 * This client always picks a random move.
 */
public class RandomMoveClient implements Client {

    private Game game = null;
    private Tile player = null;

    private MoveCalculator moveCalculator;
    private MoveExecutor moveExecutor;

    @Override
    public void receiveMap(String map) {
        this.game = GameFactory.createFromString(map);
        moveCalculator = new MoveCalculator(game);
        moveExecutor = new MoveExecutor(game);
    }

    @Override
    public void receivePlayerNumber(Tile player) {
        this.player = player;
    }

    @Override
    public Move sendMove() {
        var possibleMoves = moveCalculator.getValidMovesForPlayer(player);
        Logger.get().log("Selecting random move");
        Move chosenMove = SetUtils.getRandomElement(possibleMoves);
        Logger.get().log("Selected " + chosenMove);
        moveExecutor.executeMove(chosenMove);
        return chosenMove;
    }

    @Override
    public void receiveMove(Move move) {
        moveExecutor.executeMove(move);
    }
}
