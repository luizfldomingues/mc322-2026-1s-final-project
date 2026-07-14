package ros.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ros.application.dto.OrderCreationRequest;
import ros.infrastructure.persistence.entity.MenuItemEntity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @PersistenceContext
    private EntityManager entityManager;

    private Long savedMenuItemId;

    @BeforeEach
    void setUp() {
        MenuItemEntity item = new MenuItemEntity();
        item.setName("Burger");
        item.setDescription("Test burger");
        item.setPrice(25.0);
        item.setCategory("Burgers");
        item.setAvailable(true);
        entityManager.persist(item);
        entityManager.flush();
        savedMenuItemId = item.getId();
    }

    @Test
    void testCreateAndGetOrderIntegration() throws Exception {
        // 1. Create order
        OrderCreationRequest.OrderItemRequest itemReq = new OrderCreationRequest.OrderItemRequest(savedMenuItemId, 2);
        OrderCreationRequest request = new OrderCreationRequest("Alice", "5", List.of(itemReq));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(notNullValue()))
                .andExpect(jsonPath("$.customerName").value("Alice"))
                .andExpect(jsonPath("$.tableNumber").value("5"))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].menuItem.name").value("Burger"))
                .andExpect(jsonPath("$.items[0].quantity").value(2));

        // 2. Retrieve all orders (test serialization and retrieval)
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[?(@.customerName == 'Alice')].tableNumber").value("5"));

        // 3. Retrieve orders with min value filter (e.g. 40.0)
        mockMvc.perform(get("/api/orders").param("minValue", "40.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
                
        // 4. Retrieve orders with min value filter that excludes our order (e.g. 70.0)
        mockMvc.perform(get("/api/orders").param("minValue", "70.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
