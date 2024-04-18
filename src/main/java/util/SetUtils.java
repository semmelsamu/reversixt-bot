package util;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class SetUtils {

    // Generische Methode, um ein zufälliges Element aus einem Set zu bekommen
    public static <T> T getRandomElement(Set<T> set) {
        if (set == null || set.isEmpty()) {
            return null; // Rückgabe null, wenn das Set leer oder null ist
        }
        Random random = new Random();
        int index = random.nextInt(set.size());
        Iterator<T> iterator = set.iterator();
        T currentElement = null;
        for (int i = 0; i <= index; i++) {
            if (!iterator.hasNext()) {
                return null; // Sollte normalerweise nicht passieren, da wir die Größe des Sets überprüfen
            }
            currentElement = iterator.next();
        }
        return currentElement;
    }

}
