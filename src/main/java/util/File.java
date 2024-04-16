package util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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
            return new String(Files.readAllBytes(Paths.get(filename.replaceFirst("^/[A-Za-z]:", ""))));
        } catch (Exception e) {
            throw new RuntimeException("Error reading file " + filename);
        }
    }

    /**
     * Get all maps.
     */
    public static List<String> getAllMaps() {
        List<String> mapFiles = new ArrayList<>();
        try {
            ClassLoader classLoader = File.class.getClassLoader();
            Path mapsPath =
                    Paths.get(Objects.requireNonNull(classLoader.getResource("maps")).toURI());
            try (Stream<Path> paths = Files.walk(mapsPath)) {
                mapFiles = paths.filter(Files::isRegularFile)
                        .map(Path::toString).filter(string -> string.endsWith(".map"))
                        .collect(Collectors.toList());
            }
        } catch (URISyntaxException | IOException e) {
            Logger.get().fatal(e.getMessage());
        }
        Collections.sort(mapFiles);
        return mapFiles;
    }
}
