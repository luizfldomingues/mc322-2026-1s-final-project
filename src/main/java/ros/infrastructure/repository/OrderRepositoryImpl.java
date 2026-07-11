package ros.infrastructure.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Component;
import ros.domain.model.Order;
import ros.domain.model.Filters.OrderFilter;
import ros.domain.repository.OrderRepository;

import java.util.ArrayList;
import java.util.List;

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
        return jpaOrderRepository.save(order);
    }

    @Override
    public Order findById(Long id) {
        return jpaOrderRepository.findById(id).orElse(null);
    }

    @Override
    public List<Order> findAll() {
        return jpaOrderRepository.findAll();
    }

    @Override
    public List<Order> getByFilter(OrderFilter filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Order> query = cb.createQuery(Order.class);
        Root<Order> order = query.from(Order.class);
        
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
                Join<Order, ?> itemJoin = order.join("items");
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
        return entityManager.createQuery(query).getResultList();
    }
}
