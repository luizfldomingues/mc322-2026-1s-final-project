package ros.domain.model;

import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private Long id;
    private String customerName;
    private String tableNumber;
    private List<OrderItem> items;
    private OrderStatus status;
    private LocalDateTime createdAt;
}
