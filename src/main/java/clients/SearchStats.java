package clients;

import java.util.HashMap;
import java.util.Map;

public class SearchStats {

    /**
     * Stores for each timeout that occurred its stack trace.
     */
    static int timeouts = 0;

    /**
     * Stores for each tree layer that was successfully searched its depth.
     */
    private static final Map<Integer, Integer> depths = new HashMap<>();

    static void incrementDepthsSearched(int depth) {
        depths.put(depth, depths.getOrDefault(depth, 0) + 1);
    }

    public static String summarize() {
        return "Timeouts: " + timeouts + "\nDepths searched: " + depths;
    }

}
