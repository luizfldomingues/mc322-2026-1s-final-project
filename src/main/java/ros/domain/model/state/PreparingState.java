package ros.domain.model.state;

import ros.domain.model.Order;
import ros.domain.model.OrderStatus;

/**
 * State: PREPARING — kitchen is preparing the order.
 * Items cannot be modified. Advancing moves to READY.
 */
public class PreparingState implements OrderState {

    @Override
    public String advance(Order order) {
        order.applyStatus(OrderStatus.READY);
        return "Status avançado para " + OrderStatus.READY;
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
