package ros.infrastructure.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Component;
import ros.domain.model.Order;
import ros.domain.model.Filters.OrderFilter;
import ros.domain.repository.OrderRepository;
import ros.infrastructure.exception.EntityPersistenceException;
import ros.infrastructure.persistence.entity.MenuItemEntity;
import ros.infrastructure.persistence.entity.OrderItemEntity;
import ros.infrastructure.persistence.entity.OrderEntity;
import ros.infrastructure.persistence.mapper.OrderEntityMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JPA-backed implementation of the domain {@link OrderRepository}.
 * Infrastructure-specific exceptions are thrown here; domain exceptions
 * are only thrown by the domain model itself.
 */
@Component
public class OrderRepositoryImpl implements OrderRepository {

    private final JpaOrderRepository jpaOrderRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public OrderRepositoryImpl(JpaOrderRepository jpaOrderRepository) {
        this.jpaOrderRepository = jpaOrderRepository;
    }

    @Override
    public Order save(Order order) {
        OrderEntity entity = OrderEntityMapper.toOrderEntity(order);
        if (entity.getItems() != null) {
            for (OrderItemEntity item : entity.getItems()) {
                if (item.getMenuItem() != null && item.getMenuItem().getId() != null) {
                    MenuItemEntity managedMenuItem =
                            entityManager.find(MenuItemEntity.class, item.getMenuItem().getId());
                    if (managedMenuItem == null) {
                        throw new EntityPersistenceException("MenuItem", item.getMenuItem().getId());
                    }
                    item.setMenuItem(managedMenuItem);
                }
            }
        }
        OrderEntity saved = jpaOrderRepository.save(entity);
        return OrderEntityMapper.toOrderDomain(saved);
    }

    @Override
    public Order findById(Long id) {
        return jpaOrderRepository.findById(id)
                .map(OrderEntityMapper::toOrderDomain)
                .orElse(null);
    }

    @Override
    public List<Order> findAll() {
        return jpaOrderRepository.findAll().stream()
                .map(OrderEntityMapper::toOrderDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> getByFilter(OrderFilter filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<OrderEntity> query = cb.createQuery(OrderEntity.class);
        Root<OrderEntity> order = query.from(OrderEntity.class);

        List<Predicate> predicates = new ArrayList<>();

        if (filter != null) {
            if (filter.getFromDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(order.get("createdAt"), filter.getFromDate()));
            }
            if (filter.getToDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(order.get("createdAt"), filter.getToDate()));
            }
            if (filter.getStatuses() != null && !filter.getStatuses().isEmpty()) {
                predicates.add(order.get("status").in(filter.getStatuses()));
            }
            if (filter.getMinValue() != null) {
                Join<OrderEntity, ?> itemJoin = order.join("items");
                Join<?, ?> menuItemJoin = itemJoin.join("menuItem");
                query.groupBy(
                    order.get("id"),
                    order.get("customerName"),
                    order.get("tableNumber"),
                    order.get("status"),
                    order.get("createdAt")
                );
                Expression<Number> subtotalExpr = cb.prod(itemJoin.get("quantity"), menuItemJoin.get("price"));
                Expression<Double> sumExpr = cb.sum(subtotalExpr).as(Double.class);
                query.having(cb.greaterThanOrEqualTo(sumExpr, filter.getMinValue().doubleValue()));
            }
        }

        query.where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(query).getResultList().stream()
                .map(OrderEntityMapper::toOrderDomain)
                .collect(Collectors.toList());
    }
}
