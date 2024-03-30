package util;

import java.util.Date;

public class Logger {

    /**
     * The name of the logger.
     */
    public static String NAME = "Logger";

    /**
     * The minimum priority a message must have in order to be logged.
     */
    public static int PRIORITY = 0;

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
    private static void console(String color, String type, String message, int priority) {

        if(priority < PRIORITY) return;

        // Use StackTraceElement to get caller
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        StackTraceElement caller = stackTraceElements[3]; // 0: getStackTrace, 1: log, 2: log, 3: caller
        String className = caller.getClassName();
        String methodName = caller.getMethodName();

        // Get date
        Date currentDate = new Date();

        // Get elapsed time
        long currentTime = System.nanoTime();
        long timeElapsed = currentTime - lastTimeActive;
        int timeElapsedMs = (int) (timeElapsed / 1_000_000);
        lastTimeActive = currentTime;

        // Print
        System.out.println(color + "[" + NAME + "]  " + ANSI_RESET + currentDate + "  " + color + type + ANSI_YELLOW + "  [" + className + "." + methodName + "]  " + color + message + ANSI_RESET + "  " + timeElapsedMs + "ms");
    }

    /*
    |--------------------------------------------------------------------------
    | Public logging functions
    |--------------------------------------------------------------------------
    */

    /**
     * Log a fatal error and terminate the program.
     * @param message The error message.
     */
    public static void fatal(String message) {
        fatal(message, 0);
    }
    public static void fatal(String message, int priorityBonus) {
        console(ANSI_BOLD + ANSI_RED, "  FATAL", message, 5 + priorityBonus);
        System.exit(1);
    }

    /**
     * Log an error.
     * @param message The error message.
     */
    public static void error(String message) {
        error(message, 0);
    }
    public static void error(String message, int priorityBonus) {
        console(ANSI_RED, "  ERROR", message, 4 + priorityBonus);
    }

    /**
     * Log a warning.
     * @param message The warning message.
     */
    public static void warn(String message) {
        warn(message, 0);
    }
    public static void warn(String message, int priorityBonus) {
        console(ANSI_YELLOW, "WARNING", message, 3 + priorityBonus);
    }

    /**
     * Log a message.
     * @param message The message.
     */
    public static void log(String message) {
        log(message, 0);
    }
    public static void log(String message, int priorityBonus) {
        console(ANSI_GREEN, "    LOG", message, 2 + priorityBonus);
    }

    /**
     * Log a verbose message.
     * @param message The message.
     */
    public static void verbose(String message) {
        verbose(message, 0);
    }
    public static void verbose(String message, int priorityBonus) {
        console(ANSI_CYAN, "VERBOSE", message, 1 + priorityBonus);
    }

    /**
     * Log a debug message, unless DEBUG is set to false.
     * @param message The message.
     */
    public static void debug(String message) {
        debug(message, 0);
    }
    public static void debug(String message, int priorityBonus) {
        console(ANSI_PURPLE, "  DEBUG", message, priorityBonus);
    }
}
