package ros.infrastructure.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ros.application.dto.MenuItemRequest;
import ros.application.service.MenuApplicationService;
import ros.domain.model.MenuItem;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
public class MenuController {

    private final MenuApplicationService menuApplicationService;

    public MenuController(MenuApplicationService menuApplicationService) {
        this.menuApplicationService = menuApplicationService;
    }

    @GetMapping
    public ResponseEntity<List<MenuItem>> getMenu(@RequestParam(value = "includeUnavailable", required = false, defaultValue = "false") boolean includeUnavailable) {
        if (includeUnavailable) {
            return ResponseEntity.ok(menuApplicationService.getAllMenuItems());
        }
        return ResponseEntity.ok(menuApplicationService.getAllAvailableMenuItems());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItem> getMenuItemById(@PathVariable Long id) {
        return ResponseEntity.ok(menuApplicationService.getMenuItemById(id));
    }

    @PostMapping
    public ResponseEntity<MenuItem> createItem(@RequestBody MenuItemRequest request) {
        MenuItem created = menuApplicationService.createMenuItem(
                request.name(),
                request.description(),
                request.price(),
                request.category()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuItem> updateItem(@PathVariable Long id, @RequestBody MenuItemRequest request) {
        MenuItem updated = menuApplicationService.updateMenuItem(
                id,
                request.name(),
                request.description(),
                request.price(),
                request.category(),
                request.available()
        );
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        menuApplicationService.deleteMenuItem(id);
        return ResponseEntity.noContent().build();
    }
}