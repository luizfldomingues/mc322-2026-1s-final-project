package ros.domain.model.state;

import ros.domain.model.OrderStatus;

/**
 * Factory that resolves an {@link OrderState} from the persisted {@link OrderStatus} enum.
 * Decouples state construction from persistence representation.
 */
public final class OrderStateFactory {

    private OrderStateFactory() {
        // utility class, no instances
    }

    public static OrderState fromStatus(OrderStatus status) {
        return switch (status) {
            case PENDING   -> new PendingState();
            case PREPARING -> new PreparingState();
            case READY     -> new ReadyState();
            case DELIVERED -> new DeliveredState();
            case CANCELLED -> new CancelledState();
        };
    }
}
