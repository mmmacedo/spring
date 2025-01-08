package security;

import com.spring.MainApplication;
import com.spring.entities.User;
import com.spring.exceptions.JwtValidationException;
import com.spring.security.JwtUtils;
import com.spring.services.RegisterUserService;
import com.spring.services.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = MainApplication.class)
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class JwtUtilsIntegrationTest {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RegisterUserService registerUserService;

    @Autowired
    private UserService UserService;

    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;

    @BeforeAll
    public static void setup(@Autowired RegisterUserService registerUserService) {
        registerUserService.registerNewUser("testuser", "testuser");
    }

    @Test
    public void testGenerateAndValidateJwtToken() {
        registerUserService.registerNewUser("testuser", "testuser");

        User userDetails = (User) UserService.loadUserByUsername("testuser");

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        String token = jwtUtils.generateJwtToken(authentication);
        assertNotNull(token);
        assertDoesNotThrow(() -> jwtUtils.validateJwtToken(token));
        assertEquals("testuser", jwtUtils.getUserNameFromJwtToken(token));
    }

    @Test
    public void testValidateJwtTokenWithMalformedToken() {
        String invalidToken = "invalid.jwt.token";
        assertThrows(JwtValidationException.class, () -> jwtUtils.validateJwtToken(invalidToken));
    }

    @Test
    public void testValidateJwtTokenWithExpiredToken() {
        User userDetails = (User) UserService.loadUserByUsername("testuser");
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
        String expiredToken = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().minus(1, ChronoUnit.DAYS)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        assertThrows(JwtValidationException.class, () -> jwtUtils.validateJwtToken(expiredToken));
    }


    @Test
    public void testGetUserNameFromJwtToken() {
        User userDetails = (User) UserService.loadUserByUsername("testuser");
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        String token = jwtUtils.generateJwtToken(authentication);
        String userName = jwtUtils.getUserNameFromJwtToken(token);
        assertEquals("testuser", userName);
    }


}