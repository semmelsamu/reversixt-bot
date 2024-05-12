package exceptions;

public class OutOfTimeException extends RuntimeException {
    private final int result;

    public OutOfTimeException(String message, int result) {
        super(message);
        this.result = result;
    }

    public int getResult() {
        return result;
    }
}
