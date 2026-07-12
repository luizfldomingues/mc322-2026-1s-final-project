package ros.application.dto;

public record MenuItemRequest(
    String name,
    String description,
    Double price,
    String category,
    Boolean available
) {}
