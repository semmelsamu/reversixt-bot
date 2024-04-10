package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class File {

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Methods
    |
    |-----------------------------------------------------------------------------------------------
    */

    /**
     * Reads the contents of a file.
     *
     * @param filename The path/name of the file.
     * @return The contents of the file.
     */
    public static String readFile(String filename) {
        try {
            return new String(Files.readAllBytes(Paths.get(filename)));
        } catch (Exception e) {
            throw new RuntimeException("Error reading file " + filename);
        }
    }

    /**
     * Get all maps.
     */
    public static List<String> getAllMaps() {
        List<String> mapFiles = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(Paths.get("maps"))) {
            mapFiles = paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".map")).map(Path::toString)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Collections.sort(mapFiles);
        return mapFiles;
    }
}
