package ros.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import ros.application.dto.LoginRequest;
import ros.application.service.AuthApplicationService;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthApplicationService authApplicationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testLoginSuccess() throws Exception {
        LoginRequest request = new LoginRequest("admin", "admin");
        when(authApplicationService.login("admin", "admin")).thenReturn(true);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Login successful"));
    }

    @Test
    void testLoginFailure() throws Exception {
        LoginRequest request = new LoginRequest("admin", "wrong_password");
        when(authApplicationService.login("admin", "wrong_password")).thenReturn(false);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid credentials"));
    }

    @Test
    void testLogout() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("admin", "admin");

        mockMvc.perform(post("/api/auth/logout")
                .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string("Logout successful"));

        assertTrue(session.isInvalid());
    }
}
