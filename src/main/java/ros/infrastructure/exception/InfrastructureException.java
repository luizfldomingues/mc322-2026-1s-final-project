package ros.infrastructure.exception;

/**
 * Base exception for infrastructure-layer errors.
 * Thrown when a low-level operation (database, serialization, etc.) fails
 * in a way that is not caused by a domain invariant violation.
 */
public class InfrastructureException extends RuntimeException {
    public InfrastructureException(String message) {
        super(message);
    }

    public InfrastructureException(String message, Throwable cause) {
        super(message, cause);
    }
}
