package ros.domain.model;

import ros.domain.exception.InvalidQuantityException;

/**
 * Value-object-like entity representing one line in an {@link Order}.
 *
 * <p>Encapsulation rules:</p>
 * <ul>
 *   <li>{@code id} is assigned by the persistence layer and never exposed for mutation.</li>
 *   <li>{@code menuItem} and {@code priceAtPurchase} are fixed at creation time;
 *       only {@code quantity} may change through the domain method {@link #changeQuantity}.</li>
 *   <li>{@code order} back-reference is set internally when the item is added to an order.</li>
 * </ul>
 */
public class OrderItem {

    private Long id;
    private final MenuItem menuItem;
    private final Double priceAtPurchase;
    private int quantity;
    private Order order;

    /**
     * Creation constructor — called when a new item is added to an order.
     * Captures the menu item's price at the moment of purchase.
     */
    public OrderItem(MenuItem menuItem, int quantity, Order order) {
        if (menuItem == null) throw new IllegalArgumentException("menuItem não pode ser nulo.");
        this.menuItem          = menuItem;
        this.priceAtPurchase   = menuItem.getPrice();
        this.order             = order;
        changeQuantity(quantity);   // validates > 0
    }

    /**
     * Reconstruction constructor — used exclusively by the persistence mapper.
     */
    public OrderItem(Long id, MenuItem menuItem, int quantity, Double priceAtPurchase, Order order) {
        this.id                = id;
        this.menuItem          = menuItem;
        this.priceAtPurchase   = priceAtPurchase;
        this.order             = order;
        this.quantity          = quantity; // mapper supplies a valid, pre-validated value
    }

    // --- Domain business methods ---

    /**
     * Updates the quantity of this item, enforcing the positive-quantity invariant.
     */
    public void changeQuantity(int newQuantity) {
        if (newQuantity <= 0) throw new InvalidQuantityException();
        this.quantity = newQuantity;
    }

    /**
     * Sets the back-reference to the owning order.
     * Called internally by {@link Order} when the item is added or hydrated.
     */
    void assignOrder(Order order) {
        this.order = order;
    }

    public Double getSubtotal() {
        return this.quantity * this.priceAtPurchase;
    }

    @Override
    public String toString() {
        return "OrderItem{menuItem=" + (menuItem != null ? menuItem.getName() : "null")
                + ", quantity=" + quantity
                + ", priceAtPurchase=" + priceAtPurchase
                + ", subtotal=" + getSubtotal() + "}";
    }

    // --- Getters only (no public setters) ---

    public Long getId() {
        return id;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public Double getPriceAtPurchase() {
        return priceAtPurchase;
    }

    public int getQuantity() {
        return quantity;
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public Order getOrder() {
        return order;
    }
}
