package ros.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ros.infrastructure.persistence.entity.OrderEntity;

@Repository
public interface JpaOrderRepository extends JpaRepository<OrderEntity, Long> {
}
