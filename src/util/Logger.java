package util;

import java.lang.reflect.Method;
import java.util.Date;

public class Logger {
    // Farbkonstanten für die Konsolenausgabe
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_WHITE = "\u001B[37m";

    private static final String ANSI_BOLD = "\u001B[1m";

    // Methode zum Loggen einer Nachricht mit Farbe und Kontextinformationen
    private static void console(String color, String type, String message) {
        // StackTraceElement verwenden, um Informationen zur Ausführung zu erhalten
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        StackTraceElement caller = stackTraceElements[3]; // 0: getStackTrace, 1: log, 2: Aufrufer
        String className = caller.getClassName();
        String methodName = caller.getMethodName();

        Date currentDate = new Date();

        // Ausgabe von Klassenname, Methodennamen, Nachricht und Farbe
        System.out.println(color + "[revxt-ss24-g04] \t" + ANSI_RESET + currentDate + " \t" + color + type + ANSI_YELLOW + "\t [" + className + "." + methodName + "] " + color + message + ANSI_RESET);
    }

    public static void log(String message) {
        console(ANSI_GREEN, "LOG  ", message);
    }

    public static void warning(String message) {
        console(ANSI_YELLOW, "WARNING", message);
    }

    public static void error(String message) {
        console(ANSI_RED, "ERROR", message);
    }

    public static void fatal(String message) {
        console(ANSI_BOLD + ANSI_RED, "FATAL", message);
        System.exit(1);
    }

    public static void verbose(String message) {
        console(ANSI_CYAN, "VERBOSE", message);
    }

    public static void debug(String message) {
        console(ANSI_PURPLE, "DEBUG", message);
    }
}
