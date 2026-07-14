package ros.infrastructure.web;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ros.application.dto.OrderCreationRequest;
import ros.application.service.OrderApplicationService;
import ros.domain.model.Filters.OrderFilter;
import ros.domain.model.Order;
import ros.domain.model.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderApplicationService orderApplicationService;

    public OrderController(OrderApplicationService orderApplicationService) {
        this.orderApplicationService = orderApplicationService;
    }

    @GetMapping
    public ResponseEntity<List<Order>> getOrders(
            @RequestParam(value = "statuses", required = false) List<OrderStatus> statuses,
            @RequestParam(value = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(value = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
            @RequestParam(value = "minValue", required = false) Double minValue) {

        if (statuses == null && fromDate == null && toDate == null && minValue == null) {
            return ResponseEntity.ok(orderApplicationService.getAllOrders());
        }

        OrderFilter filter = new OrderFilter(fromDate, toDate, statuses, minValue);
        return ResponseEntity.ok(orderApplicationService.getOrdersByFilter(filter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderApplicationService.getOrderById(id));
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody OrderCreationRequest request) {
        Order created = orderApplicationService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Order> advanceOrderStatus(@PathVariable Long id) {
        Order updated = orderApplicationService.advanceOrderStatus(id);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody OrderCreationRequest request) {
        Order updated = orderApplicationService.updateOrder(id, request);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Order> cancelOrder(@PathVariable Long id) {
        Order updated = orderApplicationService.cancelOrder(id);
        return ResponseEntity.ok(updated);
    }
}