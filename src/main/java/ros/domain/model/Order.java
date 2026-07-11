package ros.domain.model;

import java.time.LocalDateTime;
import java.util.List;
import ros.domain.model.OrderItem;
import ros.domain.model.OrderStatus;

public class Order {
    private Long id;
    private String customerName;
    private String tableNumber;
    private List<OrderItem> items;
    private OrderStatus status;
    private LocalDateTime createdAt;

    public Order(Long id, String customerName, String tableNumber, List<OrderItem> items, OrderStatus status,
            LocalDateTime createdAt) {
        this.id = id;
        this.customerName = customerName;
        this.tableNumber = tableNumber;
        this.items = items;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Order status cannot be null");
        }
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void addOrderItem(OrderItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Order item cannot be null");
        }
        this.items.add(item);
    }

    public double calculateTotalValue() {
        return items.stream().mapToDouble(OrderItem::getSubtotal).sum();
    }

}
