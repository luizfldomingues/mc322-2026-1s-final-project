package ros.infrastructure.repository;

import org.springframework.stereotype.Component;
import ros.domain.model.MenuItem;
import ros.domain.repository.MenuItemRepository;
import ros.infrastructure.persistence.entity.MenuItemEntity;
import ros.infrastructure.persistence.mapper.OrderEntityMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class MenuItemRepositoryImpl implements MenuItemRepository {
    private final JpaMenuItemRepository jpaMenuItemRepository;

    public MenuItemRepositoryImpl(JpaMenuItemRepository jpaMenuItemRepository) {
        this.jpaMenuItemRepository = jpaMenuItemRepository;
    }

    @Override
    public MenuItem save(MenuItem menuItem) {
        MenuItemEntity entity = OrderEntityMapper.toEntity(menuItem);
        MenuItemEntity saved = jpaMenuItemRepository.save(entity);
        return OrderEntityMapper.toDomain(saved);
    }

    @Override
    public Optional<MenuItem> findById(Long id) {
        return jpaMenuItemRepository.findById(id)
                .map(OrderEntityMapper::toDomain);
    }

    @Override
    public List<MenuItem> findAll() {
        return jpaMenuItemRepository.findAll().stream()
                .map(OrderEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<MenuItem> findAllAvailable() {
        return jpaMenuItemRepository.findByAvailableTrue().stream()
                .map(OrderEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        jpaMenuItemRepository.deleteById(id);
    }
}
