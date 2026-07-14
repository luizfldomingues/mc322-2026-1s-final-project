package ros.domain.model.state;

import ros.domain.exception.InvalidOrderStateException;
import ros.domain.model.Order;
import ros.domain.model.OrderStatus;

/**
 * State: DELIVERED — order has been delivered to the customer.
 * Terminal state: cannot advance or cancel a delivered order.
 */
public class DeliveredState implements OrderState {

    @Override
    public String advance(Order order) {
        throw new InvalidOrderStateException(
                "Não é possível avançar um pedido que já foi entregue (status: " + OrderStatus.DELIVERED + ").");
    }

    @Override
    public void cancel(Order order) {
        throw new InvalidOrderStateException(
                "Não é possível cancelar um pedido que já foi entregue.");
    }

    @Override
    public boolean canModifyItems() {
        return false;
    }
}
