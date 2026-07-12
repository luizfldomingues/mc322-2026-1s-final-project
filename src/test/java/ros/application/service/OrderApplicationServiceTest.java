package ros.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ros.application.dto.OrderCreationRequest;
import ros.domain.exception.ItemNotFoundException;
import ros.domain.model.MenuItem;
import ros.domain.model.Order;
import ros.domain.repository.MenuItemRepository;
import ros.domain.repository.OrderRepository;

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
        orderRepository = mock(OrderRepository.class);
        menuItemRepository = mock(MenuItemRepository.class);
        orderApplicationService = new OrderApplicationService(orderRepository, menuItemRepository);
    }

    @Test
    void testCreateOrderSuccessfully() {
        MenuItem item = new MenuItem(1L, "Hamburguer", "Delicioso", 20.0, "Burgers", true);
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderCreationRequest.OrderItemRequest itemReq = new OrderCreationRequest.OrderItemRequest(1L, 2);
        OrderCreationRequest request = new OrderCreationRequest("John Doe", "12", List.of(itemReq));

        Order order = orderApplicationService.createOrder(request);

        assertNotNull(order);
        assertEquals("John Doe", order.getCustomerName());
        assertEquals("12", order.getTableNumber());
        assertEquals(1, order.getItems().size());
        assertEquals(40.0, order.calculateTotal());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void testCreateOrderWithNonExistentItemThrowsException() {
        when(menuItemRepository.findById(1L)).thenReturn(Optional.empty());

        OrderCreationRequest.OrderItemRequest itemReq = new OrderCreationRequest.OrderItemRequest(1L, 2);
        OrderCreationRequest request = new OrderCreationRequest("John Doe", "12", List.of(itemReq));

        assertThrows(ItemNotFoundException.class, () -> orderApplicationService.createOrder(request));
        verify(orderRepository, never()).save(any(Order.class));
    }
}
