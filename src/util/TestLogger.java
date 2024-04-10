package util;

public class TestLogger {

    /*
    |-----------------------------------------------------------------------------------------------
    |
    |   Singleton
    |
    |-----------------------------------------------------------------------------------------------
    */

    public static final Logger logger = new Logger("Testing", 0);

    public static Logger get() {
        return logger;
    }

}
