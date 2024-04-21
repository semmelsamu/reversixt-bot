import exercises.Exercise02;
import exercises.Exercise04;
import util.CommandLineArguments;
import util.Logger;

import java.io.IOException;
import java.util.Arrays;

public class Main {

    private static final String VERSION = "0.4";

    public static void main(String[] args) {

        // Initialize Logger
        Logger.get().name = "revxt-ss24-g04-v" + VERSION;
        Logger.get().log("Starting");

        var parsedArgs = CommandLineArguments.parse(args);

        // Launch whatever
        Exercise04.abnahme(parsedArgs.getOrDefault("-i", "127.0.0.1"),
                Integer.parseInt(parsedArgs.getOrDefault("-p", "7777")));
    }
}