package clients;

import exceptions.NotEnoughTimeException;
import exceptions.OutOfTimeException;
import util.Logger;
import util.Timer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static util.Tree.calculateBranchingFactor;
import static util.Tree.calculateNodeCountOfTree;

public class SearchStats {

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
     * Stores for each timeout that occurred its stack trace.
     */
    private static int timeouts = 0;

    /**
     * Stores for each tree layer that was successfully searched its depth.
     */
    private static final Map<Integer, Integer> depths = new HashMap<>();

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

    int firstDepthNodeCount;

    static int timePerMove = 0;

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Constructor and reset
    |
    |-----------------------------------------------------------------------------------------------
    */

    public SearchStats(int timeLimit) {
        mainTimer = new Timer(timeLimit);
    }

    void reset() {
        currentTimer = new Timer();
        currentNodeCount = 1;
        bombPhasesReached = 0;
        firstDepthNodeCount = 0;
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Time checks
    |
    |-----------------------------------------------------------------------------------------------
    */

    void checkAbort(int depth) throws NotEnoughTimeException {

        calculateTimePerMove();

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
    void checkTime() throws OutOfTimeException {
        if (mainTimer.isUp()) {
            calculateTimePerMove();
            throw new OutOfTimeException("Out of time");
        }
    }

    void calculateTimePerMove() {
        if (firstDepthNodeCount == 0) {
            timePerMove = Integer.MAX_VALUE;
            return;
        }
        timePerMove = (int) (currentTimer.timePassed() / firstDepthNodeCount);
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Setters
    |
    |-----------------------------------------------------------------------------------------------
    */

    void incrementDepthsSearched(int depth) {
        depths.put(depth, depths.getOrDefault(depth, 0) + 1);
    }

    void incrementBombPhasesReached() {
        bombPhasesReached++;
    }

    void incrementNodeCount() {
        currentNodeCount++;
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Utility
    |
    |-----------------------------------------------------------------------------------------------
    */

    public static String summarize() {
        return "Timeouts: " + timeouts + "\nDepths searched: " + depths;
    }

    private static <T> String countElements(List<T> list) {
        Map<T, Integer> stats = new HashMap<>();
        for (T item : list) {
            stats.put(item, stats.getOrDefault(item, 0) + 1);
        }
        StringBuilder result = new StringBuilder().append(list.size());
        for (Map.Entry<T, Integer> entry : stats.entrySet()) {
            result.append("\n- ").append(entry.getKey()).append(": ").append(entry.getValue());
        }
        return result.toString();
    }

}
