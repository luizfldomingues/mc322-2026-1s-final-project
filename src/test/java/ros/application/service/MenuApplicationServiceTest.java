package ros.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ros.domain.exception.ItemNotFoundException;
import ros.domain.model.MenuItem;
import ros.domain.repository.MenuItemRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MenuApplicationServiceTest {

    private MenuItemRepository menuItemRepository;
    private MenuApplicationService menuApplicationService;

    @BeforeEach
    void setUp() {
        menuItemRepository = mock(MenuItemRepository.class);
        menuApplicationService = new MenuApplicationService(menuItemRepository);
    }

    @Test
    void testGetAllMenuItems() {
        MenuItem item = new MenuItem(1L, "Hamburguer", "Delicioso", 20.0, "Burgers", true);
        when(menuItemRepository.findAll()).thenReturn(List.of(item));

        List<MenuItem> items = menuApplicationService.getAllMenuItems();

        assertEquals(1, items.size());
        assertEquals("Hamburguer", items.get(0).getName());
    }

    @Test
    void testGetMenuItemByIdNotFoundThrowsException() {
        when(menuItemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> menuApplicationService.getMenuItemById(99L));
    }
}
