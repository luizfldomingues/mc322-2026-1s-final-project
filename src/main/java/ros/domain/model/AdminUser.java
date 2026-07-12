package ros.domain.model;

import ros.domain.exception.InvalidFieldException;
import ros.domain.model.Auxiliary.HasherInterface;

public class AdminUser {
    private Long id;
    private String username;
    private String passwordHash;

    public AdminUser() {}

    public AdminUser(String username, String passwordHash) {
        setUsername(username);
        this.passwordHash = passwordHash;
    }

    public AdminUser(Long id, String username, String passwordHash) {
        this.id = id;
        setUsername(username);
        this.passwordHash = passwordHash;
    }

    // --- Domain business methods ---

    public boolean verifyPassword(String inputPassword, HasherInterface hasher) {
        return hasher.match(passwordHash, inputPassword);
    }

    public void updatePassword(String inputPassword, HasherInterface hasher) {
        if (inputPassword == null || inputPassword.isBlank())
            throw new InvalidFieldException("senha");
        this.passwordHash = hasher.encode(inputPassword);
    }

    @Override
    public String toString() {
        return "AdminUser{id=" + id + ", username='" + username + "'}";
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
        if (username == null || username.isBlank())
            throw new InvalidFieldException("username");
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
