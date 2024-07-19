package util;

public class StringUtil {

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

    public static String formatIntToFitLength(int number, int length) {
        return (new StringBuilder()).append(number).append(" ".repeat(length)).substring(0, length);
    }

}
