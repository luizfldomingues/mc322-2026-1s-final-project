package ros.infrastructure.exception;

/**
 * Thrown when a required entity is not found in the database at the infrastructure level.
 * Distinct from the domain-level {@code ItemNotFoundException} — this applies to any JPA entity
 * that the repository is asked to load but cannot find.
 */
public class EntityPersistenceException extends InfrastructureException {

    public EntityPersistenceException(String entityType, Object id) {
        super("Entidade '" + entityType + "' com id=" + id + " não foi encontrada na base de dados.");
    }

    public EntityPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
