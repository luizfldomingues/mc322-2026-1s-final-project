package ros.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import ros.domain.exception.DuplicateItemException;
import ros.domain.exception.InvalidFieldException;
import ros.domain.exception.InvalidOrderStateException;
import ros.domain.exception.InvalidQuantityException;
import ros.domain.exception.ItemNotAvailableException;
import ros.domain.exception.ItemNotFoundException;

public class Order {
    private Long id;
    private String customerName;
    private String tableNumber;
    private List<OrderItem> items = new ArrayList<>();
    private OrderStatus status = OrderStatus.PENDING;
    private LocalDateTime createdAt;

    public Order() {}

    public Order(String customerName, String tableNumber, LocalDateTime createdAt) {
        setCustomerName(customerName);
        setTableNumber(tableNumber);
        this.status = OrderStatus.PENDING;
        this.items = new ArrayList<>();
        this.createdAt = createdAt;
    }

    public Order(Long id, String customerName, String tableNumber, List<OrderItem> items, OrderStatus status, LocalDateTime createdAt) {
        this.id = id;
        setCustomerName(customerName);
        setTableNumber(tableNumber);
        this.items = items != null ? items : new ArrayList<>();
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
            throw new ItemNotAvailableException(menuItem.getName());
        }
        if (this.hasMenuItem(menuItem)) {
            throw new DuplicateItemException(menuItem.getName());
        }
        if (quantity <= 0) {
            throw new InvalidQuantityException();
        }
        this.items.add(new OrderItem(menuItem, quantity, this));
    }

    public void removeItem(MenuItem menuItem) {
        if (this.status != OrderStatus.PENDING) {
            throw new InvalidOrderStateException("Não é possível remover itens de um pedido que já está em preparo.");
        }
        boolean removed = this.items.removeIf(item -> item.getMenuItem().equals(menuItem));
        if (!removed) {
            throw new ItemNotFoundException(menuItem.getName());
        }
    }

    public void changeItemQuantity(MenuItem menuItem, int newQuantity) {
        if (this.status != OrderStatus.PENDING) {
            throw new InvalidOrderStateException("Não é possível alterar um pedido em preparo.");
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
                item.setQuantity(newQuantity);
                return;
            }
        }
        throw new ItemNotFoundException(menuItem.getName());
    }

    public void advanceStatus() {
        if (this.status == OrderStatus.PENDING) {
            this.status = OrderStatus.PREPARING;
        } else if (this.status == OrderStatus.PREPARING) {
            this.status = OrderStatus.READY;
        } else if (this.status == OrderStatus.READY) {
            this.status = OrderStatus.DELIVERED;
        } else {
            throw new InvalidOrderStateException("Não é possível avançar um pedido que está em " + this.status + ".");
        }
    }

    public void cancel() {
        if (this.status == OrderStatus.DELIVERED) {
            throw new InvalidOrderStateException("Não é possível cancelar um pedido que já foi entregue.");
        }
        if (this.status == OrderStatus.CANCELLED) {
            throw new InvalidOrderStateException("O pedido já está cancelado.");
        }
        this.status = OrderStatus.CANCELLED;
    }

    public Double calculateTotal() {
        return this.items.stream()
                .mapToDouble(OrderItem::getSubtotal)
                .sum();
    }

    @Override
    public String toString() {
        return "Order{id=" + id + ", customerName='" + customerName + "', tableNumber='" + tableNumber
                + "', status=" + status + ", total=" + calculateTotal() + ", items=" + items.size() + "}";
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
        if (customerName == null || customerName.isBlank())
            throw new InvalidFieldException("nome do cliente");
        this.customerName = customerName;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        if (tableNumber == null || tableNumber.isBlank())
            throw new InvalidFieldException("número da mesa");
        this.tableNumber = tableNumber;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items != null ? items : new ArrayList<>();
        for (OrderItem item : this.items) {
            item.setOrder(this);
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

    private void addOrderItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }
}
