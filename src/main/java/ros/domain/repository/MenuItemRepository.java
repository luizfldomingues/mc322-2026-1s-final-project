package ros.domain.repository;

import ros.domain.model.MenuItem;

import java.util.List;
import java.util.Optional;

public interface MenuItemRepository {
    MenuItem save(MenuItem menuItem);
    Optional<MenuItem> findById(Long id);
    List<MenuItem> findAll();
    List<MenuItem> findAllAvailable();
    void deleteById(Long id);
}
