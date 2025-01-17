package security;

import com.spring.MainApplication;
import com.spring.domains.user.User;
import com.spring.exceptions.JwtValidationException;
import com.spring.security.JwtUtils;
import com.spring.core.services.RegisterUserService;
import com.spring.domains.user.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = MainApplication.class)
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class JwtUtilsIT {

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

    String adminUsername = "root";

    @Test
    public void testGenerateAndValidateJwtToken() {
        User userDetails = (User) UserService.loadUserByUsername(adminUsername);

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        String token = jwtUtils.generateJwtToken(authentication);
        assertNotNull(token);
        assertDoesNotThrow(() -> jwtUtils.validateJwtToken(token));
        assertEquals(adminUsername, jwtUtils.getUserNameFromJwtToken(token));
    }

    @Test
    public void testValidateJwtTokenWithMalformedToken() {
        String invalidToken = "invalid.jwt.token";
        assertThrows(JwtValidationException.class, () -> jwtUtils.validateJwtToken(invalidToken));
    }

    @Test
    public void testValidateJwtTokenWithExpiredToken() {
        User userDetails = (User) UserService.loadUserByUsername(adminUsername);
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
        User userDetails = (User) UserService.loadUserByUsername(adminUsername);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        String token = jwtUtils.generateJwtToken(authentication);
        String userName = jwtUtils.getUserNameFromJwtToken(token);
        assertEquals(adminUsername, userName);
    }


}