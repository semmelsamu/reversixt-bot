package exceptions;

public class FailedSelectOptionException extends RuntimeException {
    public FailedSelectOptionException(String message) {
        super(message);
    }
}
