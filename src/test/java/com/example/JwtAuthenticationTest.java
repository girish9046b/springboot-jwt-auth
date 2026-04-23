import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationTest {

    private static final String SECRET_KEY = "secret";
    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hour

    @InjectMocks
    private JwtUtil jwtUtil; // Your JwtUtil class that handles JWT operations

    @Mock
    private UserDetailsService userDetailsService;

    @BeforeEach
    public void setUp() {
        // Setup any required data and mocks here
    }

    @Test
    public void testGenerateToken() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("user");
        String token = jwtUtil.generateToken(userDetails);
        assertNotNull(token);
        assertTrue(token.startsWith("Bearer "));
    }

    @Test
    public void testValidateToken() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("user");
        String token = jwtUtil.generateToken(userDetails);
        boolean isValid = jwtUtil.validateToken(token, userDetails);
        assertTrue(isValid);
    }

    @Test
    public void testExtractUsername() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("user");
        String token = jwtUtil.generateToken(userDetails);
        String username = jwtUtil.extractUsername(token);
        assertEquals("user", username);
    }

    @Test
    public void testExtractClaims() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("user");
        String token = jwtUtil.generateToken(userDetails);
        Claims claims = jwtUtil.extractAllClaims(token);
        assertNotNull(claims);
    }

    @Test
    public void testRefreshToken() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("user");
        String token = jwtUtil.generateToken(userDetails);
        String refreshedToken = jwtUtil.refreshToken(token);
        assertNotNull(refreshedToken);
        assertNotEquals(token, refreshedToken);
    }

    @Test
    public void testAuthorizationFilter() {
        String token = jwtUtil.generateToken(mock(UserDetails.class));
        // Assuming you have an Authorization Filter class to test its filtering logic
        assertNotNull(token);
        // Implement further assertions as needed based on your authorization filter
    }
}