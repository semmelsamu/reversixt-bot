package clients;

import exceptions.NotEnoughTimeException;
import exceptions.OutOfTimeException;
import util.Logger;
import util.Timer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static util.Tree.calculateBranchingFactor;
import static util.Tree.calculateNodeCountOfTree;

public class SearchStats {

    Logger logger = new Logger(this.getClass().getName());

    private final Timer timer;

    static int currentIterationMovesOnFirstDepth;

    static double timePerMoveOnFirstDepth = -1;

    /**
     * Stores for each timeout that occurred its stack trace.
     */
    private static final List<String> stats_timeouts = new LinkedList<>();

    /**
     * Stores for each tree layer that was successfully searched its depth.
     */
    private static final List<Integer> stats_depths = new LinkedList<>();

    /**
     * Counts how often we reached the bomb phase in the tree. Used for exiting the iterative
     * deepening search.
     * TODO: Add condition in checkAbort if we reached too many bomb phases
     */
    private int bombPhasesReached;

    /**
     * Stores the start timestamp of the latest iteration in the iterative deepening search.
     */
    private long currentIterationStartTime;

    /**
     * Stores the number of nodes visited (moves executed and games evaluated) of the current
     * iteration in the iterative deepening search.
     */
    private int currentIterationNodesVisited;

    public SearchStats(int timeLimit) {
        timer = new Timer(timeLimit);
    }

    /**
     * Checks if we are over the time limit
     * @throws OutOfTimeException if we ran out of time
     */
    void checkTime() throws OutOfTimeException {
        if (timer.isUp()) {
            stats_timeouts.add(
                    "at " + Thread.currentThread().getStackTrace()[1].getMethodName() + " at " +
                            Thread.currentThread().getStackTrace()[2].getMethodName());
            throw new OutOfTimeException("Out of time");
        }
    }

    void reset() {
        currentIterationStartTime = System.currentTimeMillis();
        currentIterationNodesVisited = 1;
        bombPhasesReached = 0;
    }

    void checkAbort(int depth) throws NotEnoughTimeException {

        double totalTime = System.currentTimeMillis() - currentIterationStartTime;
        double timePerGame = totalTime / currentIterationNodesVisited;

        int branchingFactor =
                (int) Math.ceil(calculateBranchingFactor(currentIterationNodesVisited, depth));

        int newDepth = depth + 1;
        double timeEstimated = calculateNodeCountOfTree(branchingFactor, newDepth) * timePerGame;

        /*
        StringBuilder stats = new StringBuilder("Stats for depth " + depth + "\n");
        stats.append("Visited states: ").append(currentIterationNodesVisited).append("\n");
        stats.append("Total time: ").append(totalTime).append(" ms\n");
        stats.append("Time per state: ").append(timePerGame).append(" ms\n");
        stats.append("Average branching factor: ").append(branchingFactor).append("\n");

        logger.verbose(stats.toString());

        stats = new StringBuilder("Estimation for depth " + newDepth + "\n");
        stats.append("Time passed: ").append(timePassed).append(" ms\n");
        stats.append("Time left: ").append(timeLeft).append(" ms\n");
        stats.append("Time estimated: ").append(timeEstimated).append(" ms\n");
        logger.verbose(stats.toString());
        */

        // Hotfix - this should never happen
        if (timeEstimated < timer.timePassed() * 2) {
            // Inflate estimated time at least a little bit
            timeEstimated += timer.timePassed();
            timeEstimated *= branchingFactor;
        }

        logger.log("Time passed for last depth: " + timer.timePassed());
        logger.log("Time estimated for next depth: " + Math.round(timeEstimated));

        if (timer.timeLeft() < timeEstimated) {
            throw new NotEnoughTimeException(
                    "Estimated more time for the next depth than what's left");
        }

    }

    void incrementDepthsSearched(int depth) {
        stats_depths.add(depth);
    }

    void incrementBombPhasesReached() {
        bombPhasesReached++;
    }

    void incrementNodesVisited() {
        currentIterationNodesVisited++;
    }

    public static String summarize() {
        return "Timeouts: " + countElements(stats_timeouts) + "\nDepths searched: " +
                countElements(stats_depths);
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Utility
    |
    |-----------------------------------------------------------------------------------------------
    */

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
