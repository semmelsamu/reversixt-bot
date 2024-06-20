package clients;

import game.Game;
import move.Move;
import util.Logger;

public class Client {

    private final Logger logger = new Logger(this.getClass().getName());

    private final Game game;

    private final int playerNumber;

    public Client(Game game, int playerNumber) {
        logger.log("Hello there.");
        this.game = game;
        logger.log(game.toString());
        this.playerNumber = playerNumber;
        logger.log("We are player " + playerNumber);
    }

    public void executeMove(Move move) {
        game.executeMove(move);
    }

    public Move search(int timeLimit) {
        this.logger.log("Searching new move in " + timeLimit + "ms");
        return game.getValidMovesForCurrentPlayer().iterator().next();
    }

    public void disqualify(int playerNumber) {
        game.disqualifyPlayer(playerNumber);
    }

    public void logStats() {

    }

}
