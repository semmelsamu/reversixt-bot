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
    private static int currentNodeCount;

    public static int timePerMove = 0;

    public static int timePerBombMove = 0;

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Constructor and reset
    |
    |-----------------------------------------------------------------------------------------------
    */

    public SearchTimer(Timer timer) {
        mainTimer = timer;
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

        double timePerGame = (double) currentTimer.timePassed() / currentNodeCount;

        int branchingFactor = (int) Math.ceil(calculateBranchingFactor(currentNodeCount, depth));

        int newDepth = depth + 1;
        double timeEstimated = calculateNodeCountOfTree(branchingFactor, newDepth) * timePerGame;

        logger.log("Time passed for last depth: " + currentTimer.timePassed());

        // Hotfix - this should never happen
        if (timeEstimated < currentTimer.timePassed() * 2) {
            logger.log("Time estimated by hotfix");

            // Inflate estimated time at least a little bit
            timeEstimated += currentTimer.timePassed();
            timeEstimated *= branchingFactor;

        }

        logger.log("Time estimated for next depth: " + Math.round(timeEstimated));

        if (mainTimer.timeLeft() < timeEstimated) {
            throw new NotEnoughTimeException(
                    "Not enough time for next depth: " + Math.round(timeEstimated) + " > " +
                            mainTimer.timeLeft());
        }

    }

    void checkFirstDepth(int moves) throws NotEnoughTimeException {
        int timeEstimated = timePerMove * moves + 500;
        if ((long) timeEstimated > mainTimer.timeLeft()) {
            throw new NotEnoughTimeException(
                    "Not enough time for first depth: " + timeEstimated + " > " +
                            mainTimer.timeLeft());
        }
    }

    void checkFirstBombDepth(int moves) throws NotEnoughTimeException {
        int timeEstimated = timePerBombMove * moves + 500;
        if ((long) timeEstimated > mainTimer.timeLeft()) {
            throw new NotEnoughTimeException(
                    "Not enough time for first bomb depth: " + timeEstimated + " > " +
                            mainTimer.timeLeft());
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

    public static void incrementNodeCount() {
        currentNodeCount++;
    }

}
