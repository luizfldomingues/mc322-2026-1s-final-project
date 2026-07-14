package ros.infrastructure.persistence.mapper;

/**
 * Generic Mapper interface that standardizes the bidirectional translation
 * between domain model objects and persistence entities.
 *
 * <p>Applying Generics here demonstrates correct use of type-safe abstractions:
 * every mapper must declare the domain type {@code D} and the entity type {@code E}
 * it handles, making implicit contracts explicit.</p>
 *
 * @param <D> the domain model type (e.g. {@code Order})
 * @param <E> the persistence entity type (e.g. {@code OrderEntity})
 */
public interface Mapper<D, E> {

    /**
     * Converts a domain model to its persistence entity representation.
     *
     * @param domain the domain object (must not be null)
     * @return the corresponding entity
     */
    E toEntity(D domain);

    /**
     * Converts a persistence entity back to its domain model representation.
     *
     * @param entity the persistence entity (must not be null)
     * @return the corresponding domain object
     */
    D toDomain(E entity);
}
