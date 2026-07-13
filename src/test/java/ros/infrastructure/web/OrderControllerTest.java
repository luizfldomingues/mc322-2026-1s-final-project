package ros.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ros.application.dto.OrderCreationRequest;
import ros.application.service.OrderApplicationService;
import ros.domain.model.Filters.OrderFilter;
import ros.domain.model.Order;
import ros.domain.model.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderApplicationService orderApplicationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetOrdersWithoutFilters() throws Exception {
        Order order = new Order("John Doe", "12", LocalDateTime.now());
        when(orderApplicationService.getAllOrders()).thenReturn(List.of(order));

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerName").value("John Doe"))
                .andExpect(jsonPath("$[0].tableNumber").value("12"));
    }

    @Test
    void testGetOrdersWithFilters() throws Exception {
        Order order = new Order("John Doe", "12", LocalDateTime.now());
        when(orderApplicationService.getOrdersByFilter(any(OrderFilter.class))).thenReturn(List.of(order));

        mockMvc.perform(get("/api/orders")
                .param("statuses", "PENDING")
                .param("minValue", "15.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerName").value("John Doe"));
    }

    @Test
    void testCreateOrder() throws Exception {
        OrderCreationRequest request = new OrderCreationRequest("John Doe", "12", List.of(new OrderCreationRequest.OrderItemRequest(1L, 2)));
        Order created = new Order("John Doe", "12", LocalDateTime.now());
        when(orderApplicationService.createOrder(any(OrderCreationRequest.class))).thenReturn(created);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerName").value("John Doe"))
                .andExpect(jsonPath("$.tableNumber").value("12"));
    }

    @Test
    void testAdvanceOrderStatus() throws Exception {
        Order order = new Order("John Doe", "12", LocalDateTime.now());
        order.setStatus(OrderStatus.PREPARING);
        when(orderApplicationService.advanceOrderStatus(1L)).thenReturn(order);

        mockMvc.perform(put("/api/orders/1/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PREPARING"));
    }
}
