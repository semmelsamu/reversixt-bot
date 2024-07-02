package clients;

import java.util.HashMap;
import java.util.Map;

public class SearchStats {

    static int moveRequests = 0;

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
        return "Move requests: " + moveRequests + "\nTimeouts: " + timeouts +
                "\nDepths searched:\n" + mapToString(depths);
    }

    public static <K, V> String mapToString(Map<K, V> map) {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            result.append("- ").append(entry.getKey().toString()).append(": ")
                    .append(entry.getValue().toString()).append("\n");
        }
        return result.toString().trim();
    }

}
