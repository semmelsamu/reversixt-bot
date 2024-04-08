package util;

public class TestLogger {
    public static final Logger logger = new Logger("Testing", 2);
    public static Logger get() { return logger; }
}
