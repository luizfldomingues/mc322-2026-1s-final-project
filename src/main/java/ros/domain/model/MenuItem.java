package ros.domain.model;

public class MenuItem {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private String category;
    private Boolean available;

    public MenuItem(Long id, String name, String description, Double price, String category, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.available = available;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Double getPrice() {
        return price;
    }

    public Boolean isAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }
}
