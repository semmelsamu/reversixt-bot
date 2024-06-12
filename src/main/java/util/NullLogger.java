package util;

public class NullLogger extends Logger {

    public NullLogger(String name) {
        super(name);
    }

    public Logger replace() {
        return this;
    }

    public void error(String message) {
    }

    public void warn(String message) {
    }

    public void log(String message) {
    }

    public void verbose(String message) {
    }

    public void debug(String message) {
    }
}
