import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
public class JwtTokenProviderTest {

    @Value("${jwt.secret}")
    private String secretKey;

    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGenerateToken() {
        String username = "user";
        String token = jwtTokenProvider.generateToken(username);
        assertNotNull(token);
        assertTrue(token.startsWith("ey")); // JWT tokens typically start with "ey"
    }

    @Test
    public void testValidateToken() {
        String username = "user";
        String token = jwtTokenProvider.generateToken(username);
        assertTrue(jwtTokenProvider.validateToken(token, username));
    }

    @Test
    public void testValidateTokenExpired() {
        String token = Jwts.builder()
                .setSubject("user")
                .setExpiration(new Date(System.currentTimeMillis() - 1000)) // expired token
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
        assertFalse(jwtTokenProvider.validateToken(token, "user"));
    }

    @Test
    public void testExtractClaims() {
        String token = jwtTokenProvider.generateToken("user");
        Claims claims = jwtTokenProvider.extractClaims(token);
        assertEquals("user", claims.getSubject());
    }
}