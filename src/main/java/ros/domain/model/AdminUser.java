package ros.domain.model;

import ros.domain.model.OrderStatus;
import ros.domain.model.Order;

public class AdminUser {
    private Long id;
    private String username;
    private String passwordHash;

    public AdminUser(Long id, String username, String passwordHash) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void ChangeStatus(Order order, String Status) {
        OrderStatus newStatus;
        try {
            newStatus = OrderStatus.valueOf(Status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid order status: " + Status);
        }
        order.setStatus(newStatus);
    }

    public void ToggleMenuItemAvailability(MenuItem menuItem) {
        menuItem.setAvailable(!menuItem.isAvailable());
    }
}
