package util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StringUtil {

    /**
     * Count the occurrences of the elements in the list and represent them as a grouped list.
     */
    public static <T> String countElements(List<T> list) {
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
