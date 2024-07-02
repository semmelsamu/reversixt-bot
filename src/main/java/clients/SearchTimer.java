package clients;

import exceptions.NotEnoughTimeException;
import exceptions.OutOfTimeException;
import util.Logger;
import util.Timer;

import static util.Tree.calculateBranchingFactor;
import static util.Tree.calculateNodeCountOfTree;

public class SearchTimer {

    Logger logger = new Logger(this.getClass().getName());

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Attributes
    |
    |-----------------------------------------------------------------------------------------------
    */

    private final Timer mainTimer;

    private Timer currentTimer;

    /**
     * Counts how often we reached the bomb phase in the tree. Used for exiting the iterative
     * deepening search.
     * TODO: Add condition in checkAbort if we reached too many bomb phases
     */
    private int bombPhasesReached;

    /**
     * Stores the number of nodes visited (moves executed and games evaluated) of the current
     * iteration in the iterative deepening search.
     */
    private int currentNodeCount;

    public static int timePerMove = 0;

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Constructor and reset
    |
    |-----------------------------------------------------------------------------------------------
    */

    public SearchTimer(int timeLimit) {
        mainTimer = new Timer(timeLimit);
    }

    void reset() {
        currentTimer = new Timer();
        currentNodeCount = 1;
        bombPhasesReached = 0;
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Time checks
    |
    |-----------------------------------------------------------------------------------------------
    */

    void checkAbort(int depth) throws NotEnoughTimeException {

        double timePerGame = (double) mainTimer.timePassed() / currentNodeCount;

        int branchingFactor = (int) Math.ceil(calculateBranchingFactor(currentNodeCount, depth));

        int newDepth = depth + 1;
        double timeEstimated = calculateNodeCountOfTree(branchingFactor, newDepth) * timePerGame;

        // Hotfix - this should never happen
        if (timeEstimated < mainTimer.timePassed() * 2) {
            // Inflate estimated time at least a little bit
            timeEstimated += mainTimer.timePassed();
            timeEstimated *= branchingFactor;
        }

        logger.log("Time passed for last depth: " + mainTimer.timePassed());
        logger.log("Time estimated for next depth: " + Math.round(timeEstimated));

        if (mainTimer.timeLeft() < timeEstimated) {
            throw new NotEnoughTimeException(
                    "Estimated more time for the next depth than what's left");
        }

    }

    void checkFirstDepth(int moves) throws NotEnoughTimeException {
        int timeEstimated = timePerMove * moves + 500;
        if ((long) timeEstimated > mainTimer.timeLeft()) {
            throw new NotEnoughTimeException("Not enough time for first depth: " + timeEstimated);
        }
    }

    /**
     * Checks if we are over the time limit
     * @throws OutOfTimeException if we ran out of time
     */
    public void checkTime() throws OutOfTimeException {
        if (mainTimer.isUp()) {
            throw new OutOfTimeException("Out of time");
        }
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Setters
    |
    |-----------------------------------------------------------------------------------------------
    */

    void incrementBombPhasesReached() {
        bombPhasesReached++;
    }

    void incrementNodeCount() {
        currentNodeCount++;
    }

}
