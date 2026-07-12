package ros.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ros.infrastructure.persistence.entity.MenuItemEntity;

import java.util.List;

@Repository
public interface JpaMenuItemRepository extends JpaRepository<MenuItemEntity, Long> {
    List<MenuItemEntity> findByAvailableTrue();
}
