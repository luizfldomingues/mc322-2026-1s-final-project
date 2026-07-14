package ros.domain.model;

import org.junit.jupiter.api.Test;
import ros.domain.exception.DuplicateItemException;
import ros.domain.exception.InvalidOrderStateException;
import ros.domain.exception.InvalidQuantityException;
import ros.domain.exception.ItemNotAvailableException;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Order} aggregate root, covering domain invariants
 * and State pattern transitions.
 */
class OrderTest {

    private static final LocalDateTime NOW = LocalDateTime.now();

    private MenuItem availableItem() {
        return new MenuItem(1L, "Burger", "desc", 20.0, "Burgers", true);
    }

    private MenuItem unavailableItem() {
        return new MenuItem(2L, "Sold Out", "desc", 5.0, "Drinks", false);
    }

    // =========================================================================
    // addItem
    // =========================================================================

    @Test
    void addItem_toPendingOrder_succeeds() {
        Order order = new Order("Alice", "5", NOW);
        order.addItem(availableItem(), 2);

        assertEquals(1, order.getItems().size());
        assertEquals(40.0, order.calculateTotal());
    }

    @Test
    void addItem_toPreparingOrder_throwsInvalidOrderStateException() {
        Order order = new Order(1L, "Alice", "5", List.of(), OrderStatus.PREPARING, NOW);

        assertThrows(InvalidOrderStateException.class,
                () -> order.addItem(availableItem(), 1));
    }

    @Test
    void addItem_unavailableItem_throwsItemNotAvailableException() {
        Order order = new Order("Alice", "5", NOW);

        assertThrows(ItemNotAvailableException.class,
                () -> order.addItem(unavailableItem(), 1));
    }

    @Test
    void addItem_withZeroQuantity_throwsInvalidQuantityException() {
        Order order = new Order("Alice", "5", NOW);

        assertThrows(InvalidQuantityException.class,
                () -> order.addItem(availableItem(), 0));
    }

    @Test
    void addItem_duplicateItem_throwsDuplicateItemException() {
        Order order = new Order("Alice", "5", NOW);
        order.addItem(availableItem(), 1);

        assertThrows(DuplicateItemException.class,
                () -> order.addItem(availableItem(), 2));
    }

    // =========================================================================
    // Items list immutability
    // =========================================================================

    @Test
    void getItems_returnsUnmodifiableList() {
        Order order = new Order("Alice", "5", NOW);
        order.addItem(availableItem(), 1);

        assertThrows(UnsupportedOperationException.class,
                () -> order.getItems().add(new OrderItem(availableItem(), 1, order)));
    }

    // =========================================================================
    // advanceStatus — State Machine
    // =========================================================================

    @Test
    void advanceStatus_fromPending_movesToPreparing() {
        Order order = new Order("Alice", "5", NOW);
        order.advanceStatus();

        assertEquals(OrderStatus.PREPARING, order.getStatus());
    }

    @Test
    void advanceStatus_fromPreparing_movesToReady() {
        Order order = new Order(1L, "Alice", "5", List.of(), OrderStatus.PREPARING, NOW);
        order.advanceStatus();

        assertEquals(OrderStatus.READY, order.getStatus());
    }

    @Test
    void advanceStatus_fromReady_movesToDelivered() {
        Order order = new Order(1L, "Alice", "5", List.of(), OrderStatus.READY, NOW);
        order.advanceStatus();

        assertEquals(OrderStatus.DELIVERED, order.getStatus());
    }

    @Test
    void advanceStatus_fromDelivered_throwsInvalidOrderStateException() {
        Order order = new Order(1L, "Alice", "5", List.of(), OrderStatus.DELIVERED, NOW);

        assertThrows(InvalidOrderStateException.class, order::advanceStatus);
    }

    @Test
    void advanceStatus_fromCancelled_throwsInvalidOrderStateException() {
        Order order = new Order(1L, "Alice", "5", List.of(), OrderStatus.CANCELLED, NOW);

        assertThrows(InvalidOrderStateException.class, order::advanceStatus);
    }

    // =========================================================================
    // cancel — State Machine
    // =========================================================================

    @Test
    void cancel_fromPending_movesToCancelled() {
        Order order = new Order("Alice", "5", NOW);
        order.cancel();

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void cancel_fromPreparing_movesToCancelled() {
        Order order = new Order(1L, "Alice", "5", List.of(), OrderStatus.PREPARING, NOW);
        order.cancel();

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void cancel_alreadyCancelled_throwsInvalidOrderStateException() {
        Order order = new Order(1L, "Alice", "5", List.of(), OrderStatus.CANCELLED, NOW);

        assertThrows(InvalidOrderStateException.class, order::cancel);
    }

    @Test
    void cancel_delivered_throwsInvalidOrderStateException() {
        Order order = new Order(1L, "Alice", "5", List.of(), OrderStatus.DELIVERED, NOW);

        assertThrows(InvalidOrderStateException.class, order::cancel);
    }

    // =========================================================================
    // calculateTotal
    // =========================================================================

    @Test
    void calculateTotal_withNoItems_returnsZero() {
        Order order = new Order("Alice", "5", NOW);

        assertEquals(0.0, order.calculateTotal());
    }

    @Test
    void calculateTotal_withMultipleItems_returnsCorrectSum() {
        MenuItem item1 = new MenuItem(1L, "Burger", "desc", 20.0, "Burgers", true);
        MenuItem item2 = new MenuItem(2L, "Fries", "desc", 10.0, "Sides", true);
        Order order = new Order("Alice", "5", NOW);
        order.addItem(item1, 2);
        order.addItem(item2, 3);

        // 2*20 + 3*10 = 70
        assertEquals(70.0, order.calculateTotal());
    }
}
