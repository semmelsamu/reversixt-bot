package clients;

import game.*;
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

        MoveCalculator moveCalculator = new MoveCalculator(game);

        Set<Move> possibleMoves =
                new HashSet<>(moveCalculator.getValidMovesForPlayer(player));

        if (possibleMoves.isEmpty()) {
            throw new RuntimeException("Could not calculate any possible moves :(");
        }

        logger.log("Selecting random move");
        Move chosenMove = SetUtils.getRandomElement(possibleMoves);
        logger.verbose("Selected " + chosenMove);

        return chosenMove;

    }
}
