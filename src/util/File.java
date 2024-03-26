package util;

import java.nio.file.Files;
import java.nio.file.Paths;

public class File {
    /**
     * Reads the contents of a file.
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
}
