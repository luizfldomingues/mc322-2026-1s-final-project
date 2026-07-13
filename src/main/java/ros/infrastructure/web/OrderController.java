package ros.infrastructure.web;

import java.time.LocalDateTime;
import java.util.List;

import ros.domain.model.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ros.infrastructure.repository.OrderRepositoryImpl;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderRepositoryImpl orderRepository;

    public OrderController(OrderRepositoryImpl orderRepository) {
        this.orderRepository = orderRepository;
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderRepository.findAll());
    }

    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Test endpoint is working!");
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody String status) {
        return ResponseEntity.ok(orderRepository.findById(id).advanceStatus());
    }

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestParam("name") String name, @RequestParam("table") String table,
            @RequestParam("createdAt") LocalDateTime createdAt) {
        Order order = new Order(name, table, createdAt);
        orderRepository.save(order);
        return ResponseEntity.ok("Order created successfully!");
    }
}