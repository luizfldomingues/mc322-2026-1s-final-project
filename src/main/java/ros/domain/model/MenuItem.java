package ros.domain.model;

import java.util.Objects;

import ros.domain.exception.InvalidFieldException;
import ros.domain.exception.InvalidPriceException;

public class MenuItem {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private String category;
    private Boolean available = true;

    public MenuItem() {}

    public MenuItem(String name, String description, Double price, String category) {
        setName(name);
        setPrice(price);
        setCategory(category);
        this.description = description;
        this.available = true;
    }

    public MenuItem(Long id, String name, String description, Double price, String category, Boolean available) {
        this.id = id;
        setName(name);
        setPrice(price);
        setCategory(category);
        this.description = description;
        this.available = available;
    }

    // --- Domain business methods ---

    public boolean isAvailable() {
        return Boolean.TRUE.equals(available);
    }

    public void updateAvailability(boolean available) {
        this.available = available;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuItem menuItem = (MenuItem) o;
        return this.id != null && this.id.equals(menuItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "MenuItem{id=" + id + ", name='" + name + "', price=" + price
                + ", category='" + category + "', available=" + available + "}";
    }

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank())
            throw new InvalidFieldException("nome");
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        if (price == null || price <= 0)
            throw new InvalidPriceException();
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        if (category == null || category.isBlank())
            throw new InvalidFieldException("categoria");
        this.category = category;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }
}
