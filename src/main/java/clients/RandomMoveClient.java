package clients;

import move.Move;
import network.Limit;
import util.Logger;
import util.SetUtils;

/**
 * This client always picks a random move.
 */
public class RandomMoveClient extends Client {

    Logger logger = new Logger(this.getClass().getName());

    @Override
    public Move sendMove(Limit type, int limit) {

        if (game.getValidMovesForCurrentPlayer().isEmpty()) {
            throw new RuntimeException("Could not calculate any possible moves :(");
        }

        logger.log("Selecting random move");
        Move chosenMove = SetUtils.getRandomElement(game.getValidMovesForCurrentPlayer());
        logger.verbose("Selected " + chosenMove);

        return chosenMove;

    }
}
