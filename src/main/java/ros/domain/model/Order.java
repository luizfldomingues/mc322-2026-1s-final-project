package ros.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import ros.domain.exception.InvalidOrderStateException;
import ros.domain.exception.RosDomainException;

public class Order {
    private Long id;
    private String customerName;
    private String tableNumber;
    private List<OrderItem> items = new ArrayList<>();
    private OrderStatus status = OrderStatus.PENDING;
    private LocalDateTime createdAt;

    public Order() {}

    public Order(String customerName, String tableNumber, LocalDateTime createdAt) {
        this.customerName = customerName;
        this.tableNumber = tableNumber;
        this.status = OrderStatus.PENDING;
        this.items = new ArrayList<>();
        this.createdAt = createdAt;
    }

    public Order(Long id, String customerName, String tableNumber, List<OrderItem> items, OrderStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.customerName = customerName;
        this.tableNumber = tableNumber;
        this.items = items;
        this.status = status;
        this.createdAt = createdAt;
    }

    // --- Domain business methods ---

    public boolean hasMenuItem(MenuItem menuItem) {
        return this.items.stream()
            .anyMatch(item -> item.getMenuItem().equals(menuItem));
    }

    public void addItem(MenuItem menuItem, int quantity) {
        if (this.status != OrderStatus.PENDING) {
            throw new InvalidOrderStateException("Não é possível adicionar itens a um pedido que já está em preparo.");
        }
        if (!menuItem.isAvailable()) {
            throw new RosDomainException("O item " + menuItem.getName() + " não está disponível no momento.");
        }
        if (this.hasMenuItem(menuItem)) {
            throw new RosDomainException("O item " + menuItem.getName() + " já existe no pedido");
        }
        OrderItem orderItem = new OrderItem(menuItem, quantity, this);
        this.items.add(orderItem);
    }

    public void removeItem(MenuItem menuItem) {
        if (this.status != OrderStatus.PENDING) {
            throw new InvalidOrderStateException("Não é possível remover itens de um pedido que já está em preparo.");
        }
        boolean removido = this.items.removeIf(item -> item.getMenuItem().equals(menuItem));
        if (!removido) {
            throw new RosDomainException("Este prato não está no pedido.");
        }
    }

    public void changeItemQuantity(MenuItem menuItem, int newQuantity) {
        if (this.status != OrderStatus.PENDING) {
            throw new InvalidOrderStateException("Não é possível alterar um pedido em preparo.");
        }
        if (newQuantity < 0) {
            throw new RosDomainException("A quantidade não pode ser negativa.");
        }
        if (newQuantity == 0) {
            removeItem(menuItem);
            return;
        }
        for (OrderItem item : this.items) {
            if (item.getMenuItem().equals(menuItem)) {
                item.setQuantity(newQuantity);
                return;
            }
        }
        throw new RosDomainException("Este prato não está no pedido para ter sua quantidade alterada.");
    }

    public void advanceStatus() {
        if (this.status == OrderStatus.PENDING) {
            this.status = OrderStatus.PREPARING;
        } else if (this.status == OrderStatus.PREPARING) {
            this.status = OrderStatus.READY;
        } else if (this.status == OrderStatus.READY) {
            this.status = OrderStatus.DELIVERED;
        } else {
            throw new InvalidOrderStateException("Não é possível avançar um pedido que está em " + this.status);
        }
    }

    public void cancel() {
        if (this.status == OrderStatus.DELIVERED) {
            throw new InvalidOrderStateException("Não é possível cancelar um pedido que já foi entregue.");
        }
        this.status = OrderStatus.CANCELLED;
    }

    public Double calculateTotal() {
        Double total = 0.0;
        for (OrderItem item : this.items) {
            total += item.getSubtotal();
        }
        return total;
    }

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
        if (items != null) {
            for (OrderItem item : items) {
                item.setOrder(this);
            }
        }
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void addOrderItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }
}
