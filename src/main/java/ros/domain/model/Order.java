package ros.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ros.domain.exception.DuplicateItemException;
import ros.domain.exception.InvalidFieldException;
import ros.domain.exception.InvalidOrderStateException;
import ros.domain.exception.InvalidQuantityException;
import ros.domain.exception.ItemNotAvailableException;
import ros.domain.exception.ItemNotFoundException;
import ros.domain.model.state.OrderState;
import ros.domain.model.state.OrderStateFactory;
import ros.domain.model.state.PendingState;

/**
 * Aggregate root for a restaurant order.
 *
 * <p>State transitions are handled via the <b>State Design Pattern</b>:
 * each concrete {@link OrderState} encapsulates what transitions are legal
 * in that state, eliminating procedural if-else chains and respecting the
 * Open/Closed Principle.</p>
 *
 * <p>Encapsulation rules:</p>
 * <ul>
 *   <li>The {@code id} and {@code createdAt} are set once (via the full
 *       reconstruction constructor used by the mapper) and never mutated.</li>
 *   <li>The {@code items} list is exposed as an <em>unmodifiable view</em>;
 *       all structural changes go through domain methods.</li>
 *   <li>The {@code status} is changed only through state-machine transitions.</li>
 * </ul>
 */
public class Order {

    private Long id;
    private String customerName;
    private String tableNumber;
    private List<OrderItem> items;
    private OrderStatus status;
    private LocalDateTime createdAt;

    // Transient state object — not persisted, derived from status
    private transient OrderState state;

    // --- Constructors ---

    /**
     * Creation constructor — used when creating a brand-new order.
     */
    public Order(String customerName, String tableNumber, LocalDateTime createdAt) {
        setCustomerName(customerName);
        setTableNumber(tableNumber);
        this.status = OrderStatus.PENDING;
        this.state  = new PendingState();
        this.items  = new ArrayList<>();
        this.createdAt = createdAt;
    }

    /**
     * Reconstruction constructor — used exclusively by the persistence mapper
     * to rebuild a fully-hydrated aggregate from the database.
     * Does NOT re-validate fields because they were already validated on creation.
     */
    public Order(Long id, String customerName, String tableNumber, List<OrderItem> items,
                 OrderStatus status, LocalDateTime createdAt) {
        this.id = id;
        setCustomerName(customerName);
        setTableNumber(tableNumber);
        this.items     = items != null ? new ArrayList<>(items) : new ArrayList<>();
        this.status    = status;
        this.state     = OrderStateFactory.fromStatus(status);
        this.createdAt = createdAt;
        // Link items back to this order
        for (OrderItem item : this.items) {
            item.assignOrder(this);
        }
    }

    // --- Domain business methods ---

    /**
     * Adds a new item to this order.
     * Delegates the state-based guard to {@link OrderState#canModifyItems()}.
     */
    public void addItem(MenuItem menuItem, int quantity) {
        if (!state.canModifyItems()) {
            throw new InvalidOrderStateException(
                    "Não é possível adicionar itens a um pedido com status " + this.status + ".");
        }
        if (!menuItem.isAvailable()) {
            throw new ItemNotAvailableException(menuItem.getName());
        }
        if (hasMenuItem(menuItem)) {
            throw new DuplicateItemException(menuItem.getName());
        }
        if (quantity <= 0) {
            throw new InvalidQuantityException();
        }
        this.items.add(new OrderItem(menuItem, quantity, this));
    }

    /**
     * Removes an item from this order.
     */
    public void removeItem(MenuItem menuItem) {
        if (!state.canModifyItems()) {
            throw new InvalidOrderStateException(
                    "Não é possível remover itens de um pedido com status " + this.status + ".");
        }
        boolean removed = this.items.removeIf(item -> item.getMenuItem().equals(menuItem));
        if (!removed) {
            throw new ItemNotFoundException(menuItem.getName());
        }
    }

    /**
     * Changes the quantity of an existing item.
     * Passing {@code newQuantity == 0} is equivalent to removing the item.
     */
    public void changeItemQuantity(MenuItem menuItem, int newQuantity) {
        if (!state.canModifyItems()) {
            throw new InvalidOrderStateException(
                    "Não é possível alterar itens de um pedido com status " + this.status + ".");
        }
        if (newQuantity < 0) {
            throw new InvalidQuantityException();
        }
        if (newQuantity == 0) {
            removeItem(menuItem);
            return;
        }
        for (OrderItem item : this.items) {
            if (item.getMenuItem().equals(menuItem)) {
                item.changeQuantity(newQuantity);
                return;
            }
        }
        throw new ItemNotFoundException(menuItem.getName());
    }

    /**
     * Advances the order to its next status (State pattern delegation).
     */
    public String advanceStatus() {
        return state.advance(this);
    }

    /**
     * Cancels the order (State pattern delegation).
     */
    public void cancel() {
        state.cancel(this);
    }

    /**
     * Internal hook called by concrete states to apply a status change.
     * Package-friendly to keep the state objects in the same package.
     */
    public void applyStatus(OrderStatus newStatus) {
        this.status = newStatus;
        this.state  = OrderStateFactory.fromStatus(newStatus);
    }

    public boolean hasMenuItem(MenuItem menuItem) {
        return this.items.stream()
                .anyMatch(item -> item.getMenuItem().equals(menuItem));
    }

    public Double calculateTotal() {
        return this.items.stream()
                .mapToDouble(OrderItem::getSubtotal)
                .sum();
    }

    /**
     * Replaces all items atomically (used during order updates from the service layer).
     * Only allowed when the order is still PENDING.
     */
    public void replaceItems(List<OrderItem> newItems) {
        if (!state.canModifyItems()) {
            throw new InvalidOrderStateException(
                    "Não é possível substituir itens de um pedido com status " + this.status + ".");
        }
        this.items.clear();
        if (newItems != null) {
            for (OrderItem item : newItems) {
                item.assignOrder(this);
                this.items.add(item);
            }
        }
    }

    @Override
    public String toString() {
        return "Order{id=" + id + ", customerName='" + customerName + "', tableNumber='" + tableNumber
                + "', status=" + status + ", total=" + calculateTotal() + ", items=" + items.size() + "}";
    }

    // --- Getters (no public setters for id / createdAt / status / items) ---

    public Long getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    /**
     * Validates and updates the customer name. Only allowed before the order is finalized.
     */
    public void setCustomerName(String customerName) {
        if (customerName == null || customerName.isBlank())
            throw new InvalidFieldException("nome do cliente");
        this.customerName = customerName;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    /**
     * Validates and updates the table number.
     */
    public void setTableNumber(String tableNumber) {
        if (tableNumber == null || tableNumber.isBlank())
            throw new InvalidFieldException("número da mesa");
        this.tableNumber = tableNumber;
    }

    /**
     * Returns an unmodifiable view of the item list to prevent external mutation.
     */
    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public OrderStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
