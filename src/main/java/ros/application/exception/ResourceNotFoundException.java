package ros.application.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when a resource requested by the client cannot be found at the application layer.
 * Maps to HTTP 404 Not Found.
 */
public class ResourceNotFoundException extends ApplicationException {

    private final String resourceType;
    private final Object resourceId;

    public ResourceNotFoundException(String resourceType, Object resourceId) {
        super(resourceType + " com identificador '" + resourceId + "' não foi encontrado(a).");
        this.resourceType = resourceType;
        this.resourceId   = resourceId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public Object getResourceId() {
        return resourceId;
    }

    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
