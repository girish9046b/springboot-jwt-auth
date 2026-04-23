import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

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
    public void testLogin_Success() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("user@example.com", "password");
        String token = "some.jwt.token";
        when(authService.login(any(LoginRequest.class))).thenReturn(token);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"email\": \"user@example.com\", \"password\": \"password\" }")).andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value(token));
    }

    @Test
    public void testLogin_Failure() throws Exception {
        // Arrange
        when(authService.login(any(LoginRequest.class))).thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"email\": \"wrong@example.com\", \"password\": \"wrongpassword\" }")).andDo(MockMvcResultHandlers.print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testLogout() throws Exception {
        // Arrange
        doNothing().when(authService).logout(anyString());

        // Act & Assert
        mockMvc.perform(post("/api/auth/logout")
                .header("Authorization", "Bearer some.jwt.token"))
                .andExpect(status().isOk());
    }

    @Test
    public void testRefreshToken_Success() throws Exception {
        // Arrange
        String refreshToken = "some.refresh.token";
        String newToken = "some.new.jwt.token";
        when(authService.refreshToken(anyString())).thenReturn(newToken);

        // Act & Assert
        mockMvc.perform(post("/api/auth/refresh")
                .header("Authorization", "Bearer " + refreshToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(newToken));
    }

    @Test
    public void testRefreshToken_Failure() throws Exception {
        // Arrange
        when(authService.refreshToken(anyString())).thenThrow(new InvalidTokenException("Invalid refresh token"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/refresh")
                .header("Authorization", "Bearer invalid.token"))
                .andExpect(status().isBadRequest());
    }
}
