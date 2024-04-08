package util;

public class TestLogger {
    public static final Logger logger = new Logger("Testing", 0);
    public static Logger get() { return logger; }
}
