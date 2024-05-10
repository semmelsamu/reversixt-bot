package util;

import java.util.HashMap;
import java.util.Map;

public class Logger {

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Singleton
    |
    |-----------------------------------------------------------------------------------------------
    */

    private static final Logger logger = new Logger(Logger.class.getName());

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
    |   Constructor
    |
    |-----------------------------------------------------------------------------------------------
    */

    public Logger(String name) {
        this.name = name;
        lastTimeActive = System.nanoTime();
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Attributes
    |
    |-----------------------------------------------------------------------------------------------
    */

    private final String name;

    /**
     * Stores the last time the logger was active in nanoseconds.
     */
    private long lastTimeActive;

    public static boolean useColors = true;

    private boolean replace = false;

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Priorities
    |
    |-----------------------------------------------------------------------------------------------
    */

    /**
     * Stores the priorities for each logger.
     * 0 = DEBUG / log everything,
     * 1 = VERBOSE,
     * 2 = LOG,
     * 3 = WARNING,
     * 4 = ERROR,
     * 5 = FATAL.
     */
    private static Map<String, Integer> priorities = new HashMap<>();

    public static void setPriority(String name, int priority) {
        priorities.put(name, priority);
    }

    /**
     * If no specific priority is given, this default value is used.
     */
    public static int defaultPriority;

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Internal Logic
    |
    |-----------------------------------------------------------------------------------------------
    */

    private static String trimString(String string, int maxLength) {
        if (string.length() > maxLength) {
            return string.substring(0, maxLength - 3) + "...";
        }
        return string;
    }

    public static String fillString(String string, int length) {
        string = trimString(string, length);
        return string + " ".repeat(length - string.length());
    }

    /**
     * Actual printing logic
     */
    private void console(String color, String type, String message, int priority) {

        if (priority < priorities.getOrDefault(this.name, defaultPriority)) {
            return;
        }

        // Use StackTraceElement to get caller
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        // 0: getStackTrace, 1: this function, 2: public log function, 3: caller
        StackTraceElement caller = stackTraceElements[3];

        String callerClassName =
                caller.getClassName().substring(caller.getClassName().lastIndexOf('.') + 1);
        String callerMethodName = caller.getMethodName();

        // Get date
        // Date currentDate = new Date();

        // Get elapsed time
        // long currentTime = System.nanoTime();
        // long timeElapsed = currentTime - lastTimeActive;
        // int timeElapsedMs = (int) (timeElapsed / 1_000_000);
        // lastTimeActive = currentTime;

        String terminator = replace ? "\r" : "\n";

        // Print
        System.out.print(terminator + fillString(
                "[" + trimString(callerClassName, 12) + ":" + trimString(callerMethodName, 10) + "]",
                25) + "  " + (useColors ? color : "") + type + "  " + message +
                (useColors ? ANSI_RESET : ""));

        replace = false;
    }


    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Methods
    |
    |-----------------------------------------------------------------------------------------------
    */

    public void newline() {
        System.out.println();
    }

    /**
     * Set the replace-flag for the next message. This will overwrite the latest message.
     */
    public Logger replace() {
        replace = true;
        return this;
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
        console(ANSI_YELLOW, "   WARN", message, 3);
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
