package ros.infrastructure.repository;

import org.springframework.stereotype.Component;
import ros.domain.model.AdminUser;
import ros.domain.repository.AdminUserRepository;
import ros.infrastructure.persistence.entity.AdminUserEntity;
import ros.infrastructure.persistence.mapper.AdminUserEntityMapper;

import java.util.Optional;

@Component
public class AdminUserRepositoryImpl implements AdminUserRepository {
    private final JpaAdminUserRepository jpaAdminUserRepository;

    public AdminUserRepositoryImpl(JpaAdminUserRepository jpaAdminUserRepository) {
        this.jpaAdminUserRepository = jpaAdminUserRepository;
    }

    @Override
    public AdminUser save(AdminUser adminUser) {
        AdminUserEntity entity = AdminUserEntityMapper.toEntity(adminUser);
        AdminUserEntity saved = jpaAdminUserRepository.save(entity);
        return AdminUserEntityMapper.toDomain(saved);
    }

    @Override
    public Optional<AdminUser> findById(Long id) {
        return jpaAdminUserRepository.findById(id)
                .map(AdminUserEntityMapper::toDomain);
    }

    @Override
    public Optional<AdminUser> findByUsername(String username) {
        return jpaAdminUserRepository.findByUsername(username)
                .map(AdminUserEntityMapper::toDomain);
    }
}
