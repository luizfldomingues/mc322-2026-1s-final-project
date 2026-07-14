package ros.domain.model.state;

import ros.domain.exception.InvalidOrderStateException;
import ros.domain.model.Order;
import ros.domain.model.OrderStatus;

/**
 * State: CANCELLED — order has been cancelled.
 * Terminal state: cannot advance or cancel an already cancelled order.
 */
public class CancelledState implements OrderState {

    @Override
    public String advance(Order order) {
        throw new InvalidOrderStateException(
                "Não é possível avançar um pedido que está cancelado (status: " + OrderStatus.CANCELLED + ").");
    }

    @Override
    public void cancel(Order order) {
        throw new InvalidOrderStateException("O pedido já está cancelado.");
    }

    @Override
    public boolean canModifyItems() {
        return false;
    }
}
