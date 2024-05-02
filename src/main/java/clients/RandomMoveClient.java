package clients;

import game.Game;
import game.MoveCalculator;
import move.Move;
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
    public Move sendMove(Game game, int player) {

        Set<Move> possibleMoves =
                new HashSet<>(MoveCalculator.getValidMovesForPlayer(game, player));

        if (possibleMoves.isEmpty()) {
            throw new RuntimeException("Could not calculate any possible moves :(");
        }

        logger.log("Selecting random move");
        Move chosenMove = SetUtils.getRandomElement(possibleMoves);
        logger.verbose("Selected " + chosenMove);

        return chosenMove;

    }
}
