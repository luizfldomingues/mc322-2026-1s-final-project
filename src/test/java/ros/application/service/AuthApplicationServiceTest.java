package ros.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ros.domain.model.AdminUser;
import ros.domain.model.Auxiliary.HasherInterface;
import ros.domain.repository.AdminUserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthApplicationServiceTest {

    private AdminUserRepository adminUserRepository;
    private HasherInterface passwordHasher;
    private AuthApplicationService authApplicationService;

    @BeforeEach
    void setUp() {
        adminUserRepository = mock(AdminUserRepository.class);
        passwordHasher = mock(HasherInterface.class);
        authApplicationService = new AuthApplicationService(adminUserRepository, passwordHasher);
    }

    @Test
    void testLoginSuccess() {
        AdminUser admin = new AdminUser(1L, "admin", "hashed_password");
        when(adminUserRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(passwordHasher.match("hashed_password", "raw_password")).thenReturn(true);

        boolean result = authApplicationService.login("admin", "raw_password");

        assertTrue(result);
    }

    @Test
    void testLoginFailure() {
        when(adminUserRepository.findByUsername("admin")).thenReturn(Optional.empty());

        boolean result = authApplicationService.login("admin", "raw_password");

        assertFalse(result);
    }
}
