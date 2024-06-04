package util;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Collection {

    /**
     * Generic method to get a random element from a set
     * @param set the set to get the random element from
     * @return a random element
     */
    public static <T> T getRandomElement(java.util.Set<T> set) {
        if (set == null || set.isEmpty()) {
            return null;
        }
        Random random = new Random();
        int index = random.nextInt(set.size());
        Iterator<T> iterator = set.iterator();
        T currentElement = null;
        for (int i = 0; i <= index; i++) {
            if (!iterator.hasNext()) {
                return null;
            }
            currentElement = iterator.next();
        }
        return currentElement;
    }

    /**
     * Finds the key of the map entry whose value (a set) contains a specific element
     */
    public static <K, V> K findKeyByValue(Map<K, Set<V>> map, V element) {
        for (Map.Entry<K, Set<V>> eintrag : map.entrySet()) {
            if (eintrag.getValue().contains(element)) {
                return eintrag.getKey();
            }
        }
        return null; // Fallback
    }

}
