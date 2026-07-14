package ros.domain.model;

import java.util.Objects;

import ros.domain.exception.InvalidFieldException;
import ros.domain.exception.InvalidPriceException;

/**
 * Domain entity representing an item on the restaurant's menu.
 *
 * <p>Encapsulation rules:</p>
 * <ul>
 *   <li>{@code id} is assigned by the persistence layer and never publicly mutable.</li>
 *   <li>Availability is toggled through the intentional domain method
 *       {@link #updateAvailability(boolean)}, not a raw setter.</li>
 *   <li>All business-critical fields ({@code name}, {@code price}, {@code category})
 *       are validated on every write.</li>
 * </ul>
 */
public class MenuItem {

    private Long id;
    private String name;
    private String description;
    private Double price;
    private String category;
    private Boolean available;

    // --- Constructors ---

    /**
     * Creation constructor — used when registering a new menu item.
     */
    public MenuItem(String name, String description, Double price, String category) {
        setName(name);
        setPrice(price);
        setCategory(category);
        this.description = description;
        this.available = true;
    }

    /**
     * Reconstruction constructor — used by the persistence mapper.
     */
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

    /**
     * Domain-intent method: explicitly enables or disables this menu item.
     */
    public void updateAvailability(boolean available) {
        this.available = available;
    }

    /**
     * Updates mutable fields from a request. Validation is enforced by the
     * individual setters following a Fail-Fast strategy.
     */
    public void update(String name, String description, Double price, String category, Boolean available) {
        setName(name);
        setPrice(price);
        setCategory(category);
        this.description = description;
        if (available != null) {
            this.available = available;
        }
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

    // --- Getters (no public setId, no raw setAvailable) ---

    public Long getId() {
        return id;
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
}
