package clients;

import evaluation.BoardInfo;
import evaluation.GameEvaluator;
import game.Game;
import move.Move;
import util.Logger;
import util.Timer;

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
     * Contains a Matrix that values corners
     */
    // private final Weights weights;

    /**
     * Initialize the client.
     * @param game         The game that the client should run on.
     * @param playerNumber The number of the player this client should control.
     */
    public Client(Game game, int playerNumber) {

        this.game = game;
        logger.log(logGame());
        this.playerNumber = playerNumber;

        logger.log("We are player " + playerNumber);

        boardInfo = new BoardInfo(game);
        // weights = new Weights(game);
    }

    /**
     * Receive a move that should be executed.
     * @param move The move in question.
     */
    public void executeMove(Move move) {
        game.executeMove(move);
        logger.debug(logGame());
    }

    /**
     * Search for the best move in the given time window.
     * @return The best move found in the given time window.
     */
    public Move search(Timer timer, int depthLimit) {

        SearchStats.moveRequests++;

        this.logger.log("Searching new move in " + timer.limit + "ms");

        // Check if reachableTiles has to be updated
        //if (boardInfo.hasReachableTilesToBeUpdated(game)) {
        //    logger.log("Re-calculating board info...");
        //    boardInfo.updateReachableTiles(game, (int) (timer.limit / 2));
        //}

        // Creating the evaluator with the current available data
        GameEvaluator evaluator = new GameEvaluator(boardInfo);

        // Initializing the search.
        // Using a Game clone to prevent the search messing with the synchronized Game
        return (new Search(game.clone(), timer, playerNumber, evaluator)).search(depthLimit);
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
        logger.verbose(SearchStats.summarize());
    }

    public String logGame() {
        return game.toString();
    }

}
