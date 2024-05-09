package exceptions;

public class GamePhaseNotValidException extends RuntimeException {
    public GamePhaseNotValidException(String message) {
        super(message);
    }
}
