import exercises.Exercise02;
import exercises.Exercise04;
import util.Logger;

import java.io.IOException;

public class Main {

    private static final String VERSION = "0.4";

    public static void main(String[] args) throws IOException {
        // Initialize Logger
        Logger.get().name = "revxt-ss24-g04-v" + VERSION;
        Logger.get().log("Starting");

        // Launch whatever
        Exercise04.abnahme();
    }
}