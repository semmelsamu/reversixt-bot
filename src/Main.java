import exercises.Exercise02;
import util.Logger;

public class Main {

    private static final String VERSION = "0.2";

    public static void main(String[] args) {
        // Initialize Logger
        Logger.NAME = "revxt-ss24-g04-v" + VERSION;
        Logger.PRIORITY = 0; // Set to 1 to prevent debug messages or 0 to allow
        Logger.log("Starting");

        // Launch whatever
        Exercise02.aufgabe3();
    }
}
