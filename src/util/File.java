package util;

import java.nio.file.Files;
import java.nio.file.Paths;

public class File {
    public static String readFile(String filename) {
        try {
            return new String(Files.readAllBytes(Paths.get(filename)));
        } catch (Exception e) {
            throw new RuntimeException("Error reading file " + filename);
        }
    }
}
