package ros.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ros.domain.model.Order;

@Repository
public interface JpaOrderRepository extends JpaRepository<Order, Long> {
}
