package ros.domain.model;

import ros.domain.exception.RosDomainException;

public class OrderItem {
    private Long id;
    private MenuItem menuItem;
    private Double priceAtPurchase;
    private int quantity;
    private Order order;

    public OrderItem() {}

    public OrderItem(MenuItem menuItem, int quantity, Order order) {
        this.menuItem = menuItem;
        this.priceAtPurchase = menuItem.getPrice();
        this.quantity = quantity;
        this.order = order;
    }

    public OrderItem(Long id, MenuItem menuItem, Integer quantity, Order order) {
        this.id = id;
        this.menuItem = menuItem;
        this.priceAtPurchase = menuItem != null ? menuItem.getPrice() : null;
        this.quantity = quantity;
        this.order = order;
    }

    // --- Domain business methods ---

    public Double getSubtotal() {
        return this.quantity * this.priceAtPurchase;
    }

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public Double getPriceAtPurchase() {
        return priceAtPurchase;
    }

    public void setPriceAtPurchase(Double priceAtPurchase) {
        this.priceAtPurchase = priceAtPurchase;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0) throw new RosDomainException("A quantidade do item deve ser positiva");
        this.quantity = quantity;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
