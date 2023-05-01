package gameapi.listener.base.exceptions;

public class GameEventException extends RuntimeException {
    private final Throwable cause;

    public GameEventException(Throwable throwable) {
        this.cause = throwable;
    }

    public GameEventException() {
        this.cause = null;
    }

    public GameEventException(Throwable cause, String message) {
        super(message);
        this.cause = cause;
    }

    public GameEventException(String message) {
        super(message);
        this.cause = null;
    }

    public Throwable getCause() {
        return this.cause;
    }
}
