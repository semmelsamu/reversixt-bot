package clients;

import board.Tile;
import game.*;
import player.Player;
import player.move.Move;
import util.Logger;
import util.SetUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * This client always picks a random move.
 */
public class RandomMoveClient implements Client {

    Logger logger = new Logger(this.getClass().getName());

    @Override
    public Move sendMove(Game game, Player player) {

        MoveCalculator moveCalculator = new MoveCalculator(game);

        Set<Move> possibleMoves = new HashSet<>();

        if (game.getGamePhase().equals(GamePhase.PHASE_1)) {
            possibleMoves.addAll(moveCalculator.getValidMovesForPlayer(player));
        } else {
            possibleMoves.addAll(moveCalculator.getAllBombMoves(player));
        }

        if (possibleMoves.isEmpty()) {
            throw new RuntimeException("Could not calculate any possible moves :(");
        }

        logger.log("Selecting random move");
        Move chosenMove = SetUtils.getRandomElement(possibleMoves);
        logger.log("Selected " + chosenMove);

        return chosenMove;

    }
}
