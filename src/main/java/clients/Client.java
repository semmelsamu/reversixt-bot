package clients;

import game.Game;
import move.Move;
import util.Logger;

public class Client {

    private final Logger logger = new Logger(this.getClass().getName());

    /**
     * The time in milliseconds by which we want to respond earlier to avoid disqualification due to
     * network latency.
     */
    private static final int TIME_BUFFER = 1000;

    /**
     * The local copy of the game that runs in sync with the server.
     */
    private final Game game;

    /**
     * The number of the player this client controls.
     */
    private final int playerNumber;

    /**
     * Initialize the client.
     * @param game         The game that the client should run on.
     * @param playerNumber The number of the player this client should control.
     */
    public Client(Game game, int playerNumber) {
        logger.log("Hello there.");
        this.game = game;
        logger.log(game.toString());
        this.playerNumber = playerNumber;
        logger.log("We are player " + playerNumber);
    }

    /**
     * Receive a move that should be executed.
     * @param move The move in question.
     */
    public void executeMove(Move move) {
        game.executeMove(move);
    }

    /**
     * Search for the best move in the given time window.
     * @param timeLimit The time limit in milliseconds
     * @return The best move found in the given time window.
     */
    public Move search(int timeLimit) {
        this.logger.log("Searching new move in " + timeLimit + "ms");
        return (new Search(game, playerNumber)).search(timeLimit - TIME_BUFFER);
    }

    /**
     * Receive a disqualification that should be noted.
     * @param playerNumber The number of the player that gets disqualified.
     */
    public void disqualify(int playerNumber) {
        game.disqualifyPlayer(playerNumber);
    }

    /**
     * Log a bunch of concluding stats.
     */
    public void logStats() {

    }

}
