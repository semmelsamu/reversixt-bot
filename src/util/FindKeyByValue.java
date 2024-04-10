package util;

import java.util.List;
import java.util.Map;

public class FindKeyByValue {

    public static <K, V> K findKeyByValue(Map<K, List<V>> map, V gesuchtesElement) {
        for (Map.Entry<K, List<V>> eintrag : map.entrySet()) {
            if (eintrag.getValue().contains(gesuchtesElement)) {
                return eintrag.getKey(); // Der Key wird zurückgegeben, wenn das Element gefunden
                // wurde
            }
        }
        return null; // null wird zurückgegeben, wenn das Element nicht gefunden wurde
    }

}
