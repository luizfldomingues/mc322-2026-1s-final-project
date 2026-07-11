package ros.domain.repository;
import java.util.List;

import ros.domain.model.Order;
import ros.domain.model.Filters.OrderFilter;

public interface OrderRepository {
    Order save(Order order);
    Order findById(Long id);
    List<Order> findAll();
    List<Order> getByFilter(OrderFilter filter);
}
