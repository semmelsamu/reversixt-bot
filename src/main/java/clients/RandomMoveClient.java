package clients;

import board.Tile;
import game.*;
import player.move.Move;
import util.Logger;
import util.SetUtils;

import java.util.Set;

/**
 * This client always picks a random move.
 */
public class RandomMoveClient implements Client {

    private Game game;
    private Tile player;

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
        if (game.getGamePhase().equals(GamePhase.PHASE_1)) {
            Set<Move> possibleMoves = moveCalculator.getValidMovesForPlayer(player);
            Logger.get().log("Selecting random move");
            Move chosenMove = SetUtils.getRandomElement(possibleMoves);
            Logger.get().log("Selected " + chosenMove);
            return chosenMove;
        } else {
            Set<Move> possibleMoves = moveCalculator.getAllBombMoves(player);
            Logger.get().log("Selecting random move");
            Move chosenMove = SetUtils.getRandomElement(possibleMoves);
            Logger.get().log("Selected " + chosenMove);
            return chosenMove;
        }
    }

    @Override
    public void receiveMove(Move move) {
        moveExecutor.executeMove(move);
    }

    @Override
    public void updateGamePhase(GamePhase gamePhase) {
        game.setGamePhase(gamePhase);
    }
}
