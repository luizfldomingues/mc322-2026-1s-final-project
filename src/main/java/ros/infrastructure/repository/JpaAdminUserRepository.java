package ros.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ros.infrastructure.persistence.entity.AdminUserEntity;

import java.util.Optional;

@Repository
public interface JpaAdminUserRepository extends JpaRepository<AdminUserEntity, Long> {
    Optional<AdminUserEntity> findByUsername(String username);
}
