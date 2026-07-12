package ros.infrastructure.persistence.mapper;

import ros.domain.model.AdminUser;
import ros.infrastructure.persistence.entity.AdminUserEntity;

public class AdminUserEntityMapper {
    public static AdminUserEntity toEntity(AdminUser model) {
        if (model == null) return null;
        return new AdminUserEntity(
            model.getId(),
            model.getUsername(),
            model.getPasswordHash()
        );
    }

    public static AdminUser toDomain(AdminUserEntity entity) {
        if (entity == null) return null;
        return new AdminUser(
            entity.getId(),
            entity.getUsername(),
            entity.getPasswordHash()
        );
    }
}
