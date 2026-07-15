package ros.domain.model.state;

import ros.domain.model.Order;
import ros.domain.model.OrderStatus;

/**
 * State: PENDING — order has been created and is awaiting preparation.
 * Items can still be added or removed. Advancing moves to PREPARING.
 */
public class PendingState implements OrderState {

    @Override
    public String advance(Order order) {
        order.applyStatus(OrderStatus.PREPARING);
        return "Status avançado para " + OrderStatus.PREPARING;
    }

    @Override
    public void cancel(Order order) {
        order.applyStatus(OrderStatus.CANCELLED);
    }

    @Override
    public boolean canModifyItems() {
        return true;
    }
}
