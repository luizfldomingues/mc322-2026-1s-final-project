package ros.domain.model.state;

import ros.domain.model.Order;
import ros.domain.model.OrderStatus;

/**
 * State: READY — order is ready for delivery.
 * Items cannot be modified. Advancing moves to DELIVERED.
 */
public class ReadyState implements OrderState {

    @Override
    public String advance(Order order) {
        order.applyStatus(OrderStatus.DELIVERED);
        return "Status avançado para " + OrderStatus.DELIVERED;
    }

    @Override
    public void cancel(Order order) {
        order.applyStatus(OrderStatus.CANCELLED);
    }

    @Override
    public boolean canModifyItems() {
        return false;
    }
}
