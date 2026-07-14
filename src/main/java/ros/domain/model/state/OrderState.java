package ros.domain.model.state;

import ros.domain.model.Order;

/**
 * State interface for the Order State Machine (State Design Pattern).
 * Each concrete state encapsulates what is allowed in that state,
 * ensuring invariants are enforced locally, never via procedural if-else chains.
 */
public interface OrderState {
    /**
     * Advances this order to its next logical status.
     * @param order the owning order
     * @return a description of the new status
     */
    String advance(Order order);

    /**
     * Cancels the order.
     * @param order the owning order
     */
    void cancel(Order order);

    /**
     * Whether the order allows structural modifications (adding/removing items).
     */
    boolean canModifyItems();
}
