package ros.domain.model;

import ros.domain.model.Auxiliary.HasherInterface;

public class AdminUser {
    private Long id;
    private String username;
    private String passwordHash;

    public AdminUser(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public boolean verifyPassword(String inputPassword, HasherInterface hasher) {
        return hasher.match(passwordHash, inputPassword);
    }

    public void updatePassword(String inputPassword, HasherInterface hasher) {
        passwordHash = hasher.encode(inputPassword);
    }
    
}
