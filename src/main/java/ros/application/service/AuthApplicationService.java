package ros.application.service;

import org.springframework.stereotype.Service;
import ros.domain.model.AdminUser;
import ros.domain.model.Auxiliary.HasherInterface;
import ros.domain.repository.AdminUserRepository;

import java.util.Optional;

@Service
public class AuthApplicationService {
    private final AdminUserRepository adminUserRepository;
    private final HasherInterface passwordHasher;

    public AuthApplicationService(AdminUserRepository adminUserRepository, HasherInterface passwordHasher) {
        this.adminUserRepository = adminUserRepository;
        this.passwordHasher = passwordHasher;
    }

    public boolean login(String username, String password) {
        Optional<AdminUser> adminOpt = adminUserRepository.findByUsername(username);
        if (adminOpt.isEmpty()) {
            return false;
        }
        AdminUser admin = adminOpt.get();
        return admin.verifyPassword(password, passwordHasher);
    }
}
