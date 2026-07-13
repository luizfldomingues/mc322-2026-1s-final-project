package ros.application.dto;

public record LoginRequest(
    String username,
    String password
) {}
