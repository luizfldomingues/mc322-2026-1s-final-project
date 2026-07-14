package ros.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ros.application.dto.OrderCreationRequest;
import ros.application.exception.ResourceNotFoundException;
import ros.domain.exception.InvalidOrderStateException;
import ros.domain.model.MenuItem;
import ros.domain.model.Order;
import ros.domain.model.OrderStatus;
import ros.domain.repository.MenuItemRepository;
import ros.domain.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderApplicationServiceTest {

    private OrderRepository orderRepository;
    private MenuItemRepository menuItemRepository;
    private OrderApplicationService orderApplicationService;

    @BeforeEach
    void setUp() {
        orderRepository      = mock(OrderRepository.class);
        menuItemRepository   = mock(MenuItemRepository.class);
        orderApplicationService = new OrderApplicationService(orderRepository, menuItemRepository);
    }

    // =========================================================================
    // createOrder
    // =========================================================================

    @Test
    void createOrder_withValidRequest_returnsOrderWithCorrectData() {
        MenuItem item = new MenuItem(1L, "Hamburguer", "Delicioso", 20.0, "Burgers", true);
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderCreationRequest request = new OrderCreationRequest("John Doe", "12",
                List.of(new OrderCreationRequest.OrderItemRequest(1L, 2)));

        Order order = orderApplicationService.createOrder(request);

        assertNotNull(order);
        assertEquals("John Doe", order.getCustomerName());
        assertEquals("12", order.getTableNumber());
        assertEquals(1, order.getItems().size());
        assertEquals(40.0, order.calculateTotal());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void createOrder_withNonExistentMenuItem_throwsResourceNotFoundException() {
        when(menuItemRepository.findById(1L)).thenReturn(Optional.empty());

        OrderCreationRequest request = new OrderCreationRequest("John Doe", "12",
                List.of(new OrderCreationRequest.OrderItemRequest(1L, 2)));

        assertThrows(ResourceNotFoundException.class,
                () -> orderApplicationService.createOrder(request));
        verify(orderRepository, never()).save(any(Order.class));
    }

    // =========================================================================
    // getOrderById
    // =========================================================================

    @Test
    void getOrderById_withNonExistentId_throwsResourceNotFoundException() {
        when(orderRepository.findById(99L)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class,
                () -> orderApplicationService.getOrderById(99L));
    }

    // =========================================================================
    // advanceOrderStatus
    // =========================================================================

    @Test
    void advanceOrderStatus_fromPending_movesToPreparing() {
        Order order = new Order("John Doe", "12", LocalDateTime.now());
        when(orderRepository.findById(1L)).thenReturn(order);
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order updated = orderApplicationService.advanceOrderStatus(1L);

        assertEquals(OrderStatus.PREPARING, updated.getStatus());
    }

    @Test
    void advanceOrderStatus_fromDelivered_throwsInvalidOrderStateException() {
        // Build an order that is already DELIVERED via the reconstruction constructor
        Order order = new Order(1L, "John Doe", "12", List.of(), OrderStatus.DELIVERED, LocalDateTime.now());
        when(orderRepository.findById(1L)).thenReturn(order);

        assertThrows(InvalidOrderStateException.class,
                () -> orderApplicationService.advanceOrderStatus(1L));
        verify(orderRepository, never()).save(any(Order.class));
    }

    // =========================================================================
    // cancelOrder
    // =========================================================================

    @Test
    void cancelOrder_fromPending_setsStatusToCancelled() {
        Order order = new Order("John Doe", "12", LocalDateTime.now());
        when(orderRepository.findById(1L)).thenReturn(order);
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order cancelled = orderApplicationService.cancelOrder(1L);

        assertEquals(OrderStatus.CANCELLED, cancelled.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void cancelOrder_alreadyCancelled_throwsInvalidOrderStateException() {
        Order order = new Order(1L, "John Doe", "12", List.of(), OrderStatus.CANCELLED, LocalDateTime.now());
        when(orderRepository.findById(1L)).thenReturn(order);

        assertThrows(InvalidOrderStateException.class,
                () -> orderApplicationService.cancelOrder(1L));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void cancelOrder_delivered_throwsInvalidOrderStateException() {
        Order order = new Order(1L, "John Doe", "12", List.of(), OrderStatus.DELIVERED, LocalDateTime.now());
        when(orderRepository.findById(1L)).thenReturn(order);

        assertThrows(InvalidOrderStateException.class,
                () -> orderApplicationService.cancelOrder(1L));
        verify(orderRepository, never()).save(any(Order.class));
    }

    // =========================================================================
    // updateOrder
    // =========================================================================

    @Test
    void updateOrder_withPendingOrder_succeeds() {
        Order order = new Order("John Doe", "12", LocalDateTime.now());
        when(orderRepository.findById(1L)).thenReturn(order);

        MenuItem item = new MenuItem(2L, "Fries", "Crispy", 10.0, "Sides", true);
        when(menuItemRepository.findById(2L)).thenReturn(Optional.of(item));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderCreationRequest updateRequest = new OrderCreationRequest("Jane Doe", "14",
                List.of(new OrderCreationRequest.OrderItemRequest(2L, 3)));

        Order updated = orderApplicationService.updateOrder(1L, updateRequest);

        assertEquals("Jane Doe", updated.getCustomerName());
        assertEquals("14", updated.getTableNumber());
        assertEquals(1, updated.getItems().size());
        assertEquals(30.0, updated.calculateTotal());
    }

    @Test
    void updateOrder_withPreparingOrder_throwsInvalidOrderStateException() {
        Order order = new Order(1L, "John Doe", "12", List.of(), OrderStatus.PREPARING, LocalDateTime.now());
        when(orderRepository.findById(1L)).thenReturn(order);

        OrderCreationRequest updateRequest = new OrderCreationRequest("Jane Doe", "14", List.of());

        assertThrows(InvalidOrderStateException.class,
                () -> orderApplicationService.updateOrder(1L, updateRequest));
        verify(orderRepository, never()).save(any(Order.class));
    }
}
