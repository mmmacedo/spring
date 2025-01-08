package config;

import com.spring.MainApplication;
import com.spring.config.WebSecurityConfig;
import com.spring.entities.User;
import com.spring.security.AuthTokenFilter;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {MainApplication.class})
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
class WebSecurityConfigTest {

    @MockBean
    private AuthenticationManager authenticationManager;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthTokenFilter authTokenFilter;

    @Autowired
    private WebSecurityConfig webSecurityConfig;

    @Autowired
    private AuthTokenFilter authenticationJwtTokenFilter;

    @Mock
    private AuthenticationConfiguration authConfig;

    @BeforeEach
    void setup() throws ServletException, IOException {
        Mockito.doNothing().when(authTokenFilter).doFilter(any(), any(), any());
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public AuthenticationManager mockAuthenticationManager() {
            return Mockito.mock(AuthenticationManager.class);
        }
    }

    @Test
    void testAuthenticationProvider() throws Exception {
        DaoAuthenticationProvider authProvider = webSecurityConfig.authenticationProvider();

        Method getUserDetailsServiceMethod = DaoAuthenticationProvider.class.getDeclaredMethod("getUserDetailsService");
        getUserDetailsServiceMethod.setAccessible(true); // Permite acesso ao método protegido
        UserDetailsService actualUserDetailsService = (UserDetailsService) getUserDetailsServiceMethod.invoke(authProvider);

        Method getPasswordEncoderMethod = DaoAuthenticationProvider.class.getDeclaredMethod("getPasswordEncoder");
        getPasswordEncoderMethod.setAccessible(true); // Permite acesso ao método protegido
        PasswordEncoder actualPasswordEncoder = (PasswordEncoder) getPasswordEncoderMethod.invoke(authProvider);

        assertNotNull(authProvider, "AuthenticationProvider should not be null");
        assertInstanceOf(BCryptPasswordEncoder.class, actualPasswordEncoder, "PasswordEncoder should be an instance of BCryptPasswordEncoder");
    }

    @Test
    void testPasswordEncoder() {
        PasswordEncoder passwordEncoder = webSecurityConfig.passwordEncoder();
        assert (passwordEncoder instanceof BCryptPasswordEncoder);
    }

    @Test
    void testAuthenticationManager() throws Exception {
        AuthenticationManager authenticationManager = webSecurityConfig.authenticationManager(authConfig);
        assert (authenticationManager != null);
    }

    @Test
    void testFilterChain_PermitAll() throws Exception {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                User.builder()
                        .id(UUID.randomUUID())
                        .username("root")
                        .password("$2a$10$vL06Lq6B3sB7.f6QeC2zF.z40ZJ21uRj9qN8g6eZtL/o18F9r7hS")
                        .build(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"root\", \"password\":\"root\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void testFilterChain_Authenticated() throws Exception {
        mockMvc.perform(get("/api/test/test"))
                .andExpect(status().isOk());
    }

    @Test
    void testAuthenticationJwtTokenFilter() {
        AuthTokenFilter tokenFilter = authenticationJwtTokenFilter;
        assertNotNull(tokenFilter, "authenticationJwtTokenFilter should not be null");
    }


}