package util;

import java.util.Date;

public class Logger {

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Singleton
    |
    |-----------------------------------------------------------------------------------------------
    */

    private static Logger logger = new Logger("Logger", 0);

    public static Logger get() {
        return logger;
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Constants
    |
    |-----------------------------------------------------------------------------------------------
    */

    /**
     * Colors
     */
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    /**
     * Weights
     */
    public static final String ANSI_BOLD = "\u001B[1m";


    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Attributes
    |
    |-----------------------------------------------------------------------------------------------
    */

    /**
     * The name of the logger. Displayed in each message which gets sent to the system out.
     */
    public String name = "Logger";

    /**
     * The minimum priority a message must have in order to be logged.
     * DEBUG=0 VERBOSE=1 LOG=2 WARNING=3 ERROR=4 FATAL=5
     */
    public int priority;

    /**
     * Stores the last time the logger was active in nanoseconds.
     */
    private long lastTimeActive;


    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Constructor
    |
    |-----------------------------------------------------------------------------------------------
    */

    public Logger(String name, int priority) {
        this.name = name;
        this.priority = priority;
        this.lastTimeActive = System.nanoTime();
    }


    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Internal Logic
    |
    |-----------------------------------------------------------------------------------------------
    */

    /**
     * Actual printing logic
     */
    private void console(String color, String type, String message, int priority) {

        if (priority < this.priority) {
            return;
        }

        // Use StackTraceElement to get caller
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        StackTraceElement caller =
                stackTraceElements[3]; // 0: getStackTrace, 1: log, 2: log, 3: caller
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
        System.out.println(
                color + "[" + name + "]  " + ANSI_RESET + currentDate + "  " + color + type +
                        ANSI_YELLOW + "  [" + className + "." + methodName + "]  " + color +
                        message + ANSI_RESET + "  " + timeElapsedMs + "ms");
    }


    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Methods
    |
    |-----------------------------------------------------------------------------------------------
    */

    /**
     * Log a fatal error and terminate the program.
     *
     * @param message The error message.
     */
    public void fatal(String message) {
        console(ANSI_BOLD + ANSI_RED, "  FATAL", message, 5);
    }

    /**
     * Log an error.
     *
     * @param message The error message.
     */
    public void error(String message) {
        console(ANSI_RED, "  ERROR", message, 4);
    }

    /**
     * Log a warning.
     *
     * @param message The warning message.
     */
    public void warn(String message) {
        console(ANSI_YELLOW, "WARNING", message, 3);
    }

    /**
     * Log a message.
     *
     * @param message The message.
     */
    public void log(String message) {
        console(ANSI_GREEN, "    LOG", message, 2);
    }

    /**
     * Log a verbose message.
     *
     * @param message The message.
     */
    public void verbose(String message) {
        console(ANSI_CYAN, "VERBOSE", message, 1);
    }

    /**
     * Log a debug message.
     *
     * @param message The message.
     */
    public void debug(String message) {
        console(ANSI_PURPLE, "  DEBUG", message, 0);
    }
}
