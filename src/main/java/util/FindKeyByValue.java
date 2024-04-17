package util;

import java.util.Map;
import java.util.Set;

public class FindKeyByValue {

    public static <K, V> K findKeyByValue(Map<K, Set<V>> map, V gesuchtesElement) {
        for (Map.Entry<K, Set<V>> eintrag : map.entrySet()) {
            if (eintrag.getValue().contains(gesuchtesElement)) {
                return eintrag.getKey();
            }
        }
        return null; // null wird zurückgegeben, wenn das Element nicht gefunden wurde
    }

}
