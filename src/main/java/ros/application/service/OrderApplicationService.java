package ros.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ros.application.dto.OrderCreationRequest;
import ros.application.exception.ResourceNotFoundException;
import ros.domain.model.MenuItem;
import ros.domain.model.Order;
import ros.domain.model.OrderItem;
import ros.domain.model.Filters.OrderFilter;
import ros.domain.exception.InvalidOrderStateException;
import ros.domain.repository.MenuItemRepository;
import ros.domain.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Application service that orchestrates order use cases.
 *
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>Loading aggregates from the repository</li>
 *   <li>Delegating business logic to the {@link Order} domain object</li>
 *   <li>Persisting changes back through the repository</li>
 *   <li>Translating infrastructure "not found" into the application-level
 *       {@link ResourceNotFoundException} (HTTP 404)</li>
 * </ul>
 *
 * <p>Domain rules (state transitions, invariants) are enforced by the
 * domain model itself — this service never contains procedural if-else
 * status checks.</p>
 */
@Service
@Transactional
public class OrderApplicationService {

    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;

    public OrderApplicationService(OrderRepository orderRepository, MenuItemRepository menuItemRepository) {
        this.orderRepository    = orderRepository;
        this.menuItemRepository = menuItemRepository;
    }

    public Order createOrder(OrderCreationRequest request) {
        Order order = new Order(request.customerName(), request.tableNumber(), LocalDateTime.now());
        for (OrderCreationRequest.OrderItemRequest itemRequest : request.items()) {
            MenuItem menuItem = resolveMenuItem(itemRequest.menuItemId());
            order.addItem(menuItem, itemRequest.quantity());
        }
        return orderRepository.save(order);
    }

    public Order getOrderById(Long id) {
        Order order = orderRepository.findById(id);
        if (order == null) {
            throw new ResourceNotFoundException("Pedido", id);
        }
        return order;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getOrdersByFilter(OrderFilter filter) {
        return orderRepository.getByFilter(filter);
    }

    public Order advanceOrderStatus(Long id) {
        Order order = getOrderById(id);
        order.advanceStatus();   // state machine enforces valid transitions
        return orderRepository.save(order);
    }

    /**
     * Replaces the full content of a PENDING order.
     * The domain object's {@code replaceItems()} enforces that the order
     * must be in PENDING state — no status check here.
     */
    public Order updateOrder(Long id, OrderCreationRequest request) {
        Order order = getOrderById(id);
        order.setCustomerName(request.customerName());
        order.setTableNumber(request.tableNumber());

        List<OrderItem> newItems = new ArrayList<>();
        for (OrderCreationRequest.OrderItemRequest itemRequest : request.items()) {
            MenuItem menuItem = resolveMenuItem(itemRequest.menuItemId());
            newItems.add(new OrderItem(menuItem, itemRequest.quantity(), null));
        }
        order.replaceItems(newItems);  // state guards are enforced by the domain
        return orderRepository.save(order);
    }

    public Order cancelOrder(Long id) {
        Order order = getOrderById(id);
        order.cancel();  // state machine enforces: cannot cancel DELIVERED or CANCELLED
        return orderRepository.save(order);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private MenuItem resolveMenuItem(Long menuItemId) {
        return menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem", menuItemId));
    }
}
