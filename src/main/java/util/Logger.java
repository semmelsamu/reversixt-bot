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
    public static final String ANSI_GRAY = "\u001B[90m";

    /**
     * Weights
     */
    public static final String ANSI_BOLD = "\u001B[1m";
    public static final String ANSI_ITALIC = "\u001B[3m";


    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Constructor
    |
    |-----------------------------------------------------------------------------------------------
    */

    public Logger(String name) {
        this.name = name;
    }

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Attributes
    |
    |-----------------------------------------------------------------------------------------------
    */

    private final String name;

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
    private static final Map<String, Integer> priorities = new HashMap<>();

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
    private void print(String color, String type, String message, int priority) {

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

        String terminator = replace ? "\r" : "\n";

        String[] lines = message.trim().split("\n");
        StringBuilder indentedMessage = new StringBuilder(lines[0]);
        for(int i = 1; i < lines.length; i++) {
            indentedMessage.append("\n").append(" ".repeat(13 + callerMethodName.length())).append(lines[i]);
        }

        // Print
        System.out.print(
                terminator + (useColors ? color : "") + fillString(type, 7) + (useColors ? ANSI_RESET : "") +
                        "  [" + callerMethodName + "]  " + (useColors ? color : "") + indentedMessage +
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
        print(ANSI_RED, "ERROR", message, 4);
    }

    /**
     * Log a warning.
     *
     * @param message The warning message.
     */
    public void warn(String message) {
        print(ANSI_YELLOW, "WARN", message, 3);
    }

    /**
     * Log a message.
     *
     * @param message The message.
     */
    public void log(String message) {
        print(ANSI_GREEN, "LOG", message, 2);
    }

    /**
     * Log a verbose message.
     *
     * @param message The message.
     */
    public void verbose(String message) {
        print(ANSI_CYAN, "VERBOSE", message, 1);
    }

    /**
     * Log a debug message.
     *
     * @param message The message.
     */
    public void debug(String message) {
        print(ANSI_PURPLE, "DEBUG", message, 0);
    }
}
