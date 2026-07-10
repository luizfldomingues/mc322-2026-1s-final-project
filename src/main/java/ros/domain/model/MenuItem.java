package ros.domain.model;

import java.util.Objects;

public class MenuItem {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private String category;
    private Boolean available;

    public MenuItem(String name, String description, Double price, String category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.available = true;
    }


    public boolean isAvailable() {
        return available;
    }

    public void updateAvailability(boolean available) {
        this.available = available;
    }

    public Double getPrice() { return this.price; }
    public String getName() { return this.name; }


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
}
