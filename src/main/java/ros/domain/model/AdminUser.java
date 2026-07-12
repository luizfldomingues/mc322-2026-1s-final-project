package ros.domain.model;

import ros.domain.model.Auxiliary.HasherInterface;

public class AdminUser {
    private Long id;
    private String username;
    private String passwordHash;

    public AdminUser() {}

    public AdminUser(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public AdminUser(Long id, String username, String passwordHash) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
    }

    // --- Domain business methods ---

    public boolean verifyPassword(String inputPassword, HasherInterface hasher) {
        return hasher.match(passwordHash, inputPassword);
    }

    public void updatePassword(String inputPassword, HasherInterface hasher) {
        passwordHash = hasher.encode(inputPassword);
    }

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
