package ros.application.dto;

import java.util.List;

public record OrderCreationRequest(
    String customerName,
    String tableNumber,
    List<OrderItemRequest> items
) {
    public record OrderItemRequest(
        Long menuItemId,
        Integer quantity
    ) {}
}