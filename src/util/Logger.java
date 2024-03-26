package util;

import java.util.Date;

public class Logger {

    /**
     * The name of the logger.
     */
    public static String LOGGER_NAME = "Logger";

    /**
     * States if the logger should log debug messages.
     */
    public static boolean DEBUG = true;

    /*
    |--------------------------------------------------------------------------
    | Static variables
    |--------------------------------------------------------------------------
    */

    /**
     * Stores the last time the logger was active in nanoseconds.
     */
    private static long lastTimeActive;

    /*
    |--------------------------------------------------------------------------
    | Formatting constants
    |--------------------------------------------------------------------------
    */

    /**
     * Colors
     */
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_WHITE = "\u001B[37m";

    /**
     * Weights
     */
    private static final String ANSI_BOLD = "\u001B[1m";

    /*
    |--------------------------------------------------------------------------
    | Static constructor
    |--------------------------------------------------------------------------
    */

    static {
        lastTimeActive = System.nanoTime();
    }

    /*
    |--------------------------------------------------------------------------
    | Internal logger
    |--------------------------------------------------------------------------
    */

    /**
     * Generate a logger message.
     * @param color The color of the log message.
     * @param type The type of the logger message.
     * @param message The actual message.
     */
    private static void console(String color, String type, String message) {

        // Use StackTraceElement to get caller
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        StackTraceElement caller = stackTraceElements[3]; // 0: getStackTrace, 1: log, 2: log, 3: caller
        String className = caller.getClassName();
        String methodName = caller.getMethodName();

        // Get date
        Date currentDate = new Date();

        long currentTime = System.nanoTime();
        long timeElapsed = currentTime - lastTimeActive;
        int timeElapsedMs = (int) (timeElapsed / 1_000_000);
        lastTimeActive = currentTime;

        // Print
        System.out.println(color + "[" + LOGGER_NAME + "]  " + ANSI_RESET + currentDate + "  " + color + type + ANSI_YELLOW + "  [" + className + "." + methodName + "]  " + color + message + ANSI_RESET + "  " + timeElapsedMs + "ms");
    }

    /*
    |--------------------------------------------------------------------------
    | Public logging functions
    |--------------------------------------------------------------------------
    */

    /**
     * Log a message.
     * @param message The message.
     */
    public static void log(String message) {
        console(ANSI_GREEN, "    LOG", message);
    }

    /**
     * Log a warning.
     * @param message The warning message.
     */
    public static void warning(String message) {
        console(ANSI_YELLOW, "WARNING", message);
    }


    /**
     * Log an error.
     * @param message The error message.
     */
    public static void error(String message) {
        console(ANSI_RED, "  ERROR", message);
    }


    /**
     * Log a fatal error and terminate the program.
     * @param message The error message.
     */
    public static void fatal(String message) {
        console(ANSI_BOLD + ANSI_RED, "  FATAL", message);
        System.exit(1);
    }


    /**
     * Log a verbose message.
     * @param message The message.
     */
    public static void verbose(String message) {
        console(ANSI_CYAN, "VERBOSE", message);
    }


    /**
     * Log a debug message, unless DEBUG is set to false.
     * @param message The message.
     */
    public static void debug(String message) {
        if(DEBUG)
            console(ANSI_PURPLE, "  DEBUG", message);
    }
}
