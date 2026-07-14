package ros.application.exception;

/**
 * Base exception for application-layer errors.
 * Thrown when a service-level operation cannot be completed due to
 * business rule violations that are orchestration-level concerns
 * (not domain invariants).
 */
public class ApplicationException extends RuntimeException {
    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
