package ros.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ros.application.dto.MenuItemRequest;
import ros.application.exception.ResourceNotFoundException;
import ros.application.service.MenuApplicationService;
import ros.domain.model.MenuItem;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MenuController.class)
@Import(GlobalExceptionHandler.class)
class MenuControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MenuApplicationService menuApplicationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAvailableMenu_returnsOk() throws Exception {
        MenuItem item = new MenuItem(1L, "Hamburguer", "Delicioso", 20.0, "Burgers", true);
        when(menuApplicationService.getAllAvailableMenuItems()).thenReturn(List.of(item));

        mockMvc.perform(get("/api/menu"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Hamburguer"))
                .andExpect(jsonPath("$[0].available").value(true));
    }

    @Test
    void createItem_withValidRequest_returnsCreated() throws Exception {
        MenuItemRequest request = new MenuItemRequest("Hamburguer", "Delicioso", 20.0, "Burgers", true);
        MenuItem created = new MenuItem(1L, "Hamburguer", "Delicioso", 20.0, "Burgers", true);
        when(menuApplicationService.createMenuItem("Hamburguer", "Delicioso", 20.0, "Burgers")).thenReturn(created);

        mockMvc.perform(post("/api/menu")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Hamburguer"));
    }

    @Test
    void updateItem_withValidRequest_returnsOk() throws Exception {
        MenuItemRequest request = new MenuItemRequest("Hamburguer", "Muito Delicioso", 25.0, "Burgers", true);
        MenuItem updated = new MenuItem(1L, "Hamburguer", "Muito Delicioso", 25.0, "Burgers", true);
        when(menuApplicationService.updateMenuItem(1L, "Hamburguer", "Muito Delicioso", 25.0, "Burgers", true)).thenReturn(updated);

        mockMvc.perform(put("/api/menu/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(25.0))
                .andExpect(jsonPath("$.description").value("Muito Delicioso"));
    }

    @Test
    void deleteItem_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/menu/1"))
                .andExpect(status().isNoContent());

        verify(menuApplicationService).deleteMenuItem(1L);
    }

    @Test
    void getMenuItemById_withExistingId_returnsOk() throws Exception {
        MenuItem item = new MenuItem(1L, "Hamburguer", "Delicioso", 20.0, "Burgers", true);
        when(menuApplicationService.getMenuItemById(1L)).thenReturn(item);

        mockMvc.perform(get("/api/menu/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Hamburguer"))
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getMenuItemById_withNonExistentId_returnsNotFound() throws Exception {
        when(menuApplicationService.getMenuItemById(99L))
                .thenThrow(new ResourceNotFoundException("MenuItem", 99L));

        mockMvc.perform(get("/api/menu/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getMenuWithIncludeUnavailable_returnsAllItems() throws Exception {
        MenuItem item1 = new MenuItem(1L, "Hamburguer", "Delicioso", 20.0, "Burgers", true);
        MenuItem item2 = new MenuItem(2L, "Pizza", "Muzzarella", 35.0, "Pizzas", false);
        when(menuApplicationService.getAllMenuItems()).thenReturn(List.of(item1, item2));

        mockMvc.perform(get("/api/menu").param("includeUnavailable", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[1].name").value("Pizza"))
                .andExpect(jsonPath("$[1].available").value(false));
    }
}
