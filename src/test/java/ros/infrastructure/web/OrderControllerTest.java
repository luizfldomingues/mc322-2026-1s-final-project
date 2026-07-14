package ros.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ros.application.dto.OrderCreationRequest;
import ros.application.exception.ResourceNotFoundException;
import ros.application.service.OrderApplicationService;
import ros.domain.exception.InvalidOrderStateException;
import ros.domain.model.Filters.OrderFilter;
import ros.domain.model.Order;
import ros.domain.model.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@Import(GlobalExceptionHandler.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderApplicationService orderApplicationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getOrders_withoutFilters_returnsOk() throws Exception {
        Order order = new Order("John Doe", "12", LocalDateTime.now());
        when(orderApplicationService.getAllOrders()).thenReturn(List.of(order));

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerName").value("John Doe"))
                .andExpect(jsonPath("$[0].tableNumber").value("12"));
    }

    @Test
    void getOrders_withFilters_returnsOk() throws Exception {
        Order order = new Order("John Doe", "12", LocalDateTime.now());
        when(orderApplicationService.getOrdersByFilter(any(OrderFilter.class))).thenReturn(List.of(order));

        mockMvc.perform(get("/api/orders")
                .param("statuses", "PENDING")
                .param("minValue", "15.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerName").value("John Doe"));
    }

    @Test
    void createOrder_withValidRequest_returnsCreated() throws Exception {
        OrderCreationRequest request = new OrderCreationRequest("John Doe", "12",
                List.of(new OrderCreationRequest.OrderItemRequest(1L, 2)));
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
    void advanceOrderStatus_returnsUpdatedOrder() throws Exception {
        // Build order in PREPARING state using reconstruction constructor
        Order order = new Order(1L, "John Doe", "12", List.of(), OrderStatus.PREPARING, LocalDateTime.now());
        when(orderApplicationService.advanceOrderStatus(1L)).thenReturn(order);

        mockMvc.perform(put("/api/orders/1/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PREPARING"));
    }

    @Test
    void advanceOrderStatus_onDeliveredOrder_returnsBadRequest() throws Exception {
        when(orderApplicationService.advanceOrderStatus(1L))
                .thenThrow(new InvalidOrderStateException("Não é possível avançar um pedido que já foi entregue."));

        mockMvc.perform(put("/api/orders/1/status"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getOrderById_withExistingId_returnsOk() throws Exception {
        Order order = new Order(1L, "John Doe", "12", List.of(), OrderStatus.PENDING, LocalDateTime.now());
        when(orderApplicationService.getOrderById(1L)).thenReturn(order);

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("John Doe"))
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getOrderById_withNonExistentId_returnsNotFound() throws Exception {
        when(orderApplicationService.getOrderById(99L))
                .thenThrow(new ResourceNotFoundException("Pedido", 99L));

        mockMvc.perform(get("/api/orders/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void updateOrder_withValidRequest_returnsOk() throws Exception {
        OrderCreationRequest request = new OrderCreationRequest("Jane Doe", "14", List.of());
        Order updated = new Order(1L, "Jane Doe", "14", List.of(), OrderStatus.PENDING, LocalDateTime.now());
        when(orderApplicationService.updateOrder(eq(1L), any(OrderCreationRequest.class))).thenReturn(updated);

        mockMvc.perform(put("/api/orders/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("Jane Doe"))
                .andExpect(jsonPath("$.tableNumber").value("14"));
    }

    @Test
    void cancelOrder_returnsOk() throws Exception {
        Order order = new Order(1L, "John Doe", "12", List.of(), OrderStatus.CANCELLED, LocalDateTime.now());
        when(orderApplicationService.cancelOrder(1L)).thenReturn(order);

        mockMvc.perform(put("/api/orders/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void cancelOrder_alreadyCancelled_returnsBadRequest() throws Exception {
        when(orderApplicationService.cancelOrder(1L))
                .thenThrow(new InvalidOrderStateException("O pedido já está cancelado."));

        mockMvc.perform(put("/api/orders/1/cancel"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("O pedido já está cancelado."));
    }
}
