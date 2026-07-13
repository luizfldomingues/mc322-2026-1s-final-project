package ros.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ros.application.dto.MenuItemRequest;
import ros.application.service.MenuApplicationService;
import ros.domain.model.MenuItem;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MenuController.class)
class MenuControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MenuApplicationService menuApplicationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAvailableMenu() throws Exception {
        MenuItem item = new MenuItem(1L, "Hamburguer", "Delicioso", 20.0, "Burgers", true);
        when(menuApplicationService.getAllAvailableMenuItems()).thenReturn(List.of(item));

        mockMvc.perform(get("/api/menu"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Hamburguer"))
                .andExpect(jsonPath("$[0].available").value(true));
    }

    @Test
    void testCreateItem() throws Exception {
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
    void testUpdateItem() throws Exception {
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
    void testDeleteItem() throws Exception {
        mockMvc.perform(delete("/api/menu/1"))
                .andExpect(status().isNoContent());

        verify(menuApplicationService).deleteMenuItem(1L);
    }
}
