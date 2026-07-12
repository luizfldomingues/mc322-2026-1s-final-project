package ros.application.service;

import org.springframework.stereotype.Service;
import ros.domain.model.MenuItem;
import ros.domain.repository.MenuItemRepository;
import ros.domain.exception.ItemNotFoundException;

import java.util.List;

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
                .orElseThrow(() -> new ItemNotFoundException("Item " + id + " não encontrado"));
    }

    public MenuItem createMenuItem(String name, String description, Double price, String category) {
        MenuItem menuItem = new MenuItem(name, description, price, category);
        return menuItemRepository.save(menuItem);
    }

    public MenuItem updateMenuItem(Long id, String name, String description, Double price, String category, Boolean available) {
        MenuItem menuItem = getMenuItemById(id);
        menuItem.setName(name);
        menuItem.setDescription(description);
        menuItem.setPrice(price);
        menuItem.setCategory(category);
        menuItem.setAvailable(available);
        return menuItemRepository.save(menuItem);
    }

    public void deleteMenuItem(Long id) {
        getMenuItemById(id);
        menuItemRepository.deleteById(id);
    }

    public MenuItem toggleAvailability(Long id, boolean available) {
        MenuItem menuItem = getMenuItemById(id);
        menuItem.updateAvailability(available);
        return menuItemRepository.save(menuItem);
    }
}
