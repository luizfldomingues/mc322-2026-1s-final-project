package ros.infrastructure.web;

import java.util.List;

import ros.domain.model.MenuItem;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ros.infrastructure.repository.MenuItemRepositoryImpl;

@RestController
@RequestMapping("/api/menu")
public class MenuController {

    private final MenuItemRepositoryImpl MenuRepository;

    public MenuController(MenuItemRepositoryImpl MenuRepository) {
        this.MenuRepository = MenuRepository;
    }

    @PostMapping
    public ResponseEntity<String> createItem(@RequestParam("id") Long id, @RequestParam("name") String name,
            @RequestParam("description") String description, @RequestParam("price") Double price,
            @RequestParam("category") String category, @RequestParam("available") Boolean available) {
        MenuItem menuItem = new MenuItem(id, name, description, price, category, available);
        MenuRepository.save(menuItem);
        return ResponseEntity.ok("Menu item created successfully!");
    }

    @DeleteMapping
    public ResponseEntity<String> deleteItem(@RequestParam("id") Long id) {
        MenuRepository.deleteById(id);
        return ResponseEntity.ok("Menu item deleted successfully!");
    }
}