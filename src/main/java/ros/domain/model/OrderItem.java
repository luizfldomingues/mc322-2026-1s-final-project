package ros.domain.model;

public class OrderItem {
    private Long id;
    private MenuItem menuItem;
    private Integer quantity;
    private Order order;

    public OrderItem() {}

    public OrderItem(Long id, MenuItem menuItem, Integer quantity, Order order) {
        this.id = id;
        this.menuItem = menuItem;
        this.quantity = quantity;
        this.order = order;
    }

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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Double getSubtotal() {
        return menuItem != null ? menuItem.getPrice() * quantity : 0.0;
    }
}
