package ros.infrastructure.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ros.application.exception.ResourceNotFoundException;
import ros.domain.exception.RosDomainException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Global exception handler — translates domain and application exceptions into
 * well-structured HTTP responses, implementing the <b>Fail-Fast</b> strategy
 * at the API boundary.
 *
 * <p>Without this handler, unhandled {@link RuntimeException}s bubble up and
 * produce opaque 500 Internal Server Error responses. By catching them here,
 * clients receive meaningful 4xx responses with structured JSON bodies.</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles all domain-level rule violations (e.g. cancelling an already cancelled order,
     * advancing a delivered order, adding a duplicate item).
     * Returns HTTP 400 Bad Request.
     */
    @ExceptionHandler(RosDomainException.class)
    public ResponseEntity<Map<String, Object>> handleDomainException(RosDomainException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Handles 404 Not Found errors raised by the application service when a
     * resource is requested but does not exist.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        return buildResponse(ex.getHttpStatus(), ex.getMessage());
    }

    /**
     * Handles validation errors on incoming request bodies (e.g. missing fields,
     * invalid quantity). Returns HTTP 422 Unprocessable Entity.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    /**
     * Catch-all handler. Prevents any unhandled exception from leaking an opaque
     * 500 Internal Server Error with a Java stack trace.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro inesperado. Por favor, tente novamente mais tarde.");
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}
