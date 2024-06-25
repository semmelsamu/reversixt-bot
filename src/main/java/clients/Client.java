package clients;

import evaluation.BoardInfo;
import evaluation.GameEvaluator;
import game.Game;
import move.Move;
import util.Logger;

public class Client {

    private final Logger logger = new Logger(this.getClass().getName());

    /**
     * The local copy of the game that runs in sync with the server.
     */
    private final Game game;

    /**
     * The number of the player this client controls.
     */
    private final int playerNumber;

    /**
     * Board statistics containing the progress simulation/reachable tiles and tile ratings
     */
    private final BoardInfo boardInfo;

    /**
     * Initialize the client.
     * @param game         The game that the client should run on.
     * @param playerNumber The number of the player this client should control.
     */
    public Client(Game game, int playerNumber) {
        this.game = game;
        logGame();
        this.playerNumber = playerNumber;
        logger.log("We are player " + playerNumber);

        logger.log("Calculating board info...");
        boardInfo = new BoardInfo(game);
    }

    /**
     * Receive a move that should be executed.
     * @param move The move in question.
     */
    public void executeMove(Move move) {

        game.executeMove(move);

        logGame();

    }

    /**
     * Search for the best move in the given time window.
     * @param timeLimit The time limit in milliseconds
     * @return The best move found in the given time window.
     */
    public Move search(int timeLimit) {

        this.logger.log("Searching new move in " + timeLimit + "ms");

        long startTime = System.currentTimeMillis();

        // Check if reachableTiles has to be updated
        if (boardInfo.getSimulationCount() < 2 &&
                (double) game.totalTilesOccupiedCounter.getTotalTilesOccupied() /
                        boardInfo.getReachableTiles() > 0.6) {
            logger.log("Re-calculating board info...");
            boardInfo.updateReachableTiles(game, timeLimit / 2);
        }

        // Creating the evaluator with the current available data
        GameEvaluator evaluator = new GameEvaluator(boardInfo);

        // Initializing the search.
        // Using a Game clone to prevent the search messing with the synchronized Game
        Search search = new Search(game.clone(), playerNumber, evaluator);

        long timePassed = System.currentTimeMillis() - startTime;

        // Searching
        return search.search(timeLimit - (int) timePassed);
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
        logger.verbose(Search.getStats());
    }

    public void logGame() {
        logger.log(game.toString());
        if (game.communities != null) {
            logger.log(game.communities.toString());
        }
    }

}
