package ros.domain.model;

import ros.domain.exception.RosDomainException;

public class OrderItem {
    private Long id;
    private MenuItem menuItem;
    private Double priceAtPurchase;
    private int quantity;
    private Order order;

    public OrderItem(MenuItem menuItem, int quantity, Order order) {
        this.menuItem = menuItem;
        this.priceAtPurchase = menuItem.getPrice();
        this.quantity = quantity;
        this.order = order;
    }

    public Double getSubtotal() {
        return (this.quantity) * (this.priceAtPurchase);
    }

    public MenuItem getMenuItem() { return menuItem; }
    public int getQuantity() { return quantity; }

    public void setQuantity(int newQuantity) { 
        if (newQuantity <= 0) throw new RosDomainException("A quantidade do item deve ser positiva");
        this.quantity = newQuantity;
    }
}
