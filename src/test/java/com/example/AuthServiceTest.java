import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.service.AuthService;
import com.example.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLogin() throws Exception {
        User user = new User("testuser", "password");
        when(authService.login(any())).thenReturn("fake-token");

        ResultActions response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testuser\",\"password\":\"password\"}")
        );

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-token"));
    }

    @Test
    public void testRegister() throws Exception {
        User user = new User("testuser", "password");
        when(authService.register(any())).thenReturn(user);

        ResultActions response = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testuser\",\"password\":\"password\"}")
        );

        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    public void testTokenValidation() throws Exception {
        String token = "valid-token";
        when(authService.validateToken(token)).thenReturn(true);

        ResultActions response = mockMvc.perform(get("/api/auth/validate")
                .header("Authorization", "Bearer " + token)
        );

        response.andExpect(status().isOk())
                .andExpect(content().string("Token is valid"));
    }

    @Test
    public void testRefresh() throws Exception {
        String oldToken = "old-token";
        String newToken = "new-token";
        when(authService.refreshToken(oldToken)).thenReturn(newToken);

        ResultActions response = mockMvc.perform(post("/api/auth/refresh")
                .header("Authorization", "Bearer " + oldToken)
        );

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(newToken));
    }

    @Test
    public void testLogout() throws Exception {
        String token = "valid-token";
        doNothing().when(authService).logout(token);

        ResultActions response = mockMvc.perform(post("/api/auth/logout")
                .header("Authorization", "Bearer " + token)
        );

        response.andExpect(status().isNoContent());
    }

    @Test
    public void testUsernameExtraction() {
        String token = "valid-token";
        when(authService.extractUsername(token)).thenReturn("testuser");

        String username = authService.extractUsername(token);
        assertEquals("testuser", username);
    }
}