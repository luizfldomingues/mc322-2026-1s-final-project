package ros.domain.model;

public class OrderItem {
    private Long id;
    private MenuItem menuItem;
    private Integer quantity;
    private Order order;

    public OrderItem(Long id, MenuItem menuItem, Integer quantity, Order order) {
        this.id = id;
        this.menuItem = menuItem;
        this.quantity = quantity;
        this.order = order;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Order getOrder() {
        return order;
    }

    public double getSubtotal() {
        return menuItem.getPrice() * quantity;
    }
}
