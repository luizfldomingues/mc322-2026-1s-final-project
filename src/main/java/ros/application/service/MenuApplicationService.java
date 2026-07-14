package ros.application.service;

import org.springframework.stereotype.Service;
import ros.application.exception.ResourceNotFoundException;
import ros.domain.model.MenuItem;
import ros.domain.repository.MenuItemRepository;

import java.util.List;

/**
 * Application service that orchestrates menu item use cases.
 *
 * <p>Uses the domain method {@link MenuItem#update(String, String, Double, String, Boolean)}
 * to perform updates, keeping all validation logic inside the domain model.</p>
 */
@Service
public class MenuApplicationService {

    private final MenuItemRepository menuItemRepository;

    public MenuApplicationService(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }

    public List<MenuItem> getAllAvailableMenuItems() {
        return menuItemRepository.findAllAvailable();
    }

    public MenuItem getMenuItemById(Long id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem", id));
    }

    public MenuItem createMenuItem(String name, String description, Double price, String category) {
        MenuItem menuItem = new MenuItem(name, description, price, category);
        return menuItemRepository.save(menuItem);
    }

    /**
     * Updates all mutable fields of a menu item using the domain-intent
     * {@link MenuItem#update} method, which validates each field (Fail-Fast).
     */
    public MenuItem updateMenuItem(Long id, String name, String description,
                                   Double price, String category, Boolean available) {
        MenuItem menuItem = getMenuItemById(id);
        menuItem.update(name, description, price, category, available);
        return menuItemRepository.save(menuItem);
    }

    public void deleteMenuItem(Long id) {
        getMenuItemById(id);  // ensures 404 if not found before deleting
        menuItemRepository.deleteById(id);
    }

    public MenuItem toggleAvailability(Long id, boolean available) {
        MenuItem menuItem = getMenuItemById(id);
        menuItem.updateAvailability(available);
        return menuItemRepository.save(menuItem);
    }
}
