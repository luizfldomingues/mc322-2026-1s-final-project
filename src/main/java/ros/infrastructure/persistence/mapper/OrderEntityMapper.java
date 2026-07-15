package ros.infrastructure.persistence.mapper;

import ros.domain.model.MenuItem;
import ros.domain.model.Order;
import ros.domain.model.OrderItem;
import ros.infrastructure.persistence.entity.MenuItemEntity;
import ros.infrastructure.persistence.entity.OrderEntity;
import ros.infrastructure.persistence.entity.OrderItemEntity;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Stateless mapper that translates between the domain model and JPA entities.
 *
 * <p>Implements {@link Mapper}{@code <Order, OrderEntity>} for the primary aggregate,
 * and provides static utility methods for {@link MenuItem} ↔ {@link MenuItemEntity}
 * and {@link OrderItem} ↔ {@link OrderItemEntity}.</p>
 *
 * <p>All domain objects are built through their <em>reconstruction constructors</em>
 * (never through empty constructors + setters), so invariants validated at creation
 * time are not re-applied on hydration.</p>
 */
public class OrderEntityMapper implements Mapper<Order, OrderEntity> {

    // -------------------------------------------------------------------------
    // Singleton instance (stateless, safe to share)
    // -------------------------------------------------------------------------

    public static final OrderEntityMapper INSTANCE = new OrderEntityMapper();

    private OrderEntityMapper() {}

    // -------------------------------------------------------------------------
    // Mapper<Order, OrderEntity> implementation
    // -------------------------------------------------------------------------

    @Override
    public OrderEntity toEntity(Order model) {
        return toOrderEntity(model);
    }

    @Override
    public Order toDomain(OrderEntity entity) {
        return toOrderDomain(entity);
    }

    // -------------------------------------------------------------------------
    // Static helpers (for use in repository implementations)
    // -------------------------------------------------------------------------

    public static MenuItemEntity toEntity(MenuItem model) {
        if (model == null) return null;
        return new MenuItemEntity(
                model.getId(),
                model.getName(),
                model.getDescription(),
                model.getPrice(),
                model.getCategory(),
                model.getAvailable()
        );
    }

    public static MenuItem toDomain(MenuItemEntity entity) {
        if (entity == null) return null;
        return new MenuItem(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getCategory(),
                entity.getAvailable()
        );
    }

    public static OrderItemEntity toEntity(OrderItem model, OrderEntity orderEntity) {
        if (model == null) return null;
        return new OrderItemEntity(
                model.getId(),
                toEntity(model.getMenuItem()),
                model.getQuantity(),
                model.getPriceAtPurchase(),
                orderEntity
        );
    }

    /**
     * Reconstructs a domain {@link OrderItem} from its entity counterpart.
     * Uses the full reconstruction constructor to avoid calling setters
     * that are no longer public.
     */
    public static OrderItem toDomain(OrderItemEntity entity) {
        if (entity == null) return null;
        MenuItem menuItem = toDomain(entity.getMenuItem());
        Double price = entity.getPriceAtPurchase() != null ? entity.getPriceAtPurchase() : (menuItem != null ? menuItem.getPrice() : 0.0);
        // Use reconstruction constructor: (id, menuItem, quantity, priceAtPurchase, order)
        return new OrderItem(
                entity.getId(),
                menuItem,
                entity.getQuantity(),
                price,
                null  // order back-reference is wired by Order's reconstruction constructor
        );
    }

    public static OrderEntity toOrderEntity(Order model) {
        if (model == null) return null;
        OrderEntity entity = new OrderEntity();
        entity.setId(model.getId());
        entity.setCustomerName(model.getCustomerName());
        entity.setTableNumber(model.getTableNumber());
        entity.setStatus(model.getStatus());
        entity.setCreatedAt(model.getCreatedAt());
        if (model.getItems() != null) {
            List<OrderItemEntity> itemEntities = model.getItems().stream()
                    .map(item -> toEntity(item, entity))
                    .collect(Collectors.toList());
            entity.setItems(itemEntities);
        }
        return entity;
    }

    public static Order toOrderDomain(OrderEntity entity) {
        if (entity == null) return null;
        List<OrderItem> itemModels = entity.getItems() == null
                ? List.of()
                : entity.getItems().stream()
                        .map(OrderEntityMapper::toDomain)
                        .collect(Collectors.toList());
        // Use the reconstruction constructor — it wires item→order back-references
        return new Order(
                entity.getId(),
                entity.getCustomerName(),
                entity.getTableNumber(),
                itemModels,
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}
