package ros.domain.repository;

import ros.domain.model.AdminUser;

import java.util.Optional;

public interface AdminUserRepository {
    AdminUser save(AdminUser adminUser);
    Optional<AdminUser> findById(Long id);
    Optional<AdminUser> findByUsername(String username);
}
