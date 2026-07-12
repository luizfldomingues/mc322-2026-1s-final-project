package ros.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ros.application.dto.OrderCreationRequest;
import ros.domain.exception.ItemNotFoundException;
import ros.domain.model.MenuItem;
import ros.domain.model.Order;
import ros.domain.model.Filters.OrderFilter;
import ros.domain.repository.MenuItemRepository;
import ros.domain.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class OrderApplicationService {
    
    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;

    public OrderApplicationService(OrderRepository orderRepository, MenuItemRepository menuItemRepository) {
        this.orderRepository = orderRepository;
        this.menuItemRepository = menuItemRepository;
    }

    public Order createOrder(OrderCreationRequest request) {
        Order order = new Order(request.customerName(), request.tableNumber(), LocalDateTime.now());
        for (OrderCreationRequest.OrderItemRequest itemRequest : request.items()) {
            MenuItem menuItem = menuItemRepository.findById(itemRequest.menuItemId())
                    .orElseThrow(() -> new ItemNotFoundException("Item " + itemRequest.menuItemId() + " não encontrado"));
            order.addItem(menuItem, itemRequest.quantity());
        }
        return orderRepository.save(order);
    }

    public Order getOrderById(Long id) {
        Order order = orderRepository.findById(id);
        if (order == null) {
            throw new ItemNotFoundException("Pedido " + id + " não encontrado");
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
        order.advanceStatus();
        return orderRepository.save(order);
    }

    public Order cancelOrder(Long id) {
        Order order = getOrderById(id);
        order.cancel();
        return orderRepository.save(order);
    }
}
