package ros.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ros.application.exception.ResourceNotFoundException;
import ros.domain.exception.InvalidFieldException;
import ros.domain.exception.InvalidPriceException;
import ros.domain.model.MenuItem;
import ros.domain.repository.MenuItemRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MenuApplicationServiceTest {

    private MenuItemRepository menuItemRepository;
    private MenuApplicationService menuApplicationService;

    @BeforeEach
    void setUp() {
        menuItemRepository       = mock(MenuItemRepository.class);
        menuApplicationService   = new MenuApplicationService(menuItemRepository);
    }

    // =========================================================================
    // getAllMenuItems
    // =========================================================================

    @Test
    void getAllMenuItems_returnsAllItems() {
        MenuItem item = new MenuItem(1L, "Hamburguer", "Delicioso", 20.0, "Burgers", true);
        when(menuItemRepository.findAll()).thenReturn(List.of(item));

        List<MenuItem> items = menuApplicationService.getAllMenuItems();

        assertEquals(1, items.size());
        assertEquals("Hamburguer", items.get(0).getName());
    }

    // =========================================================================
    // getMenuItemById
    // =========================================================================

    @Test
    void getMenuItemById_withNonExistentId_throwsResourceNotFoundException() {
        when(menuItemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> menuApplicationService.getMenuItemById(99L));
    }

    // =========================================================================
    // createMenuItem
    // =========================================================================

    @Test
    void createMenuItem_withValidData_returnsCreatedItem() {
        MenuItem saved = new MenuItem(1L, "Pizza", "Muzzarella", 35.0, "Pizzas", true);
        when(menuItemRepository.save(any(MenuItem.class))).thenReturn(saved);

        MenuItem result = menuApplicationService.createMenuItem("Pizza", "Muzzarella", 35.0, "Pizzas");

        assertNotNull(result);
        assertEquals("Pizza", result.getName());
        verify(menuItemRepository).save(any(MenuItem.class));
    }

    @Test
    void createMenuItem_withBlankName_throwsInvalidFieldException() {
        assertThrows(InvalidFieldException.class,
                () -> menuApplicationService.createMenuItem("", "desc", 10.0, "Cat"));
        verify(menuItemRepository, never()).save(any());
    }

    @Test
    void createMenuItem_withNegativePrice_throwsInvalidPriceException() {
        assertThrows(InvalidPriceException.class,
                () -> menuApplicationService.createMenuItem("Item", "desc", -1.0, "Cat"));
        verify(menuItemRepository, never()).save(any());
    }

    // =========================================================================
    // updateMenuItem
    // =========================================================================

    @Test
    void updateMenuItem_withValidData_updatesAllFields() {
        MenuItem existing = new MenuItem(1L, "Old Name", "Old Desc", 10.0, "Cat", true);
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(menuItemRepository.save(any(MenuItem.class))).thenAnswer(inv -> inv.getArgument(0));

        MenuItem updated = menuApplicationService.updateMenuItem(1L, "New Name", "New Desc", 25.0, "NewCat", false);

        assertEquals("New Name", updated.getName());
        assertEquals(25.0, updated.getPrice());
        assertFalse(updated.isAvailable());
    }

    // =========================================================================
    // deleteMenuItem
    // =========================================================================

    @Test
    void deleteMenuItem_withExistingId_callsDeleteById() {
        MenuItem item = new MenuItem(1L, "Hamburguer", "desc", 10.0, "Cat", true);
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(item));

        menuApplicationService.deleteMenuItem(1L);

        verify(menuItemRepository).deleteById(1L);
    }

    @Test
    void deleteMenuItem_withNonExistentId_throwsResourceNotFoundException() {
        when(menuItemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> menuApplicationService.deleteMenuItem(99L));
        verify(menuItemRepository, never()).deleteById(any());
    }
}
