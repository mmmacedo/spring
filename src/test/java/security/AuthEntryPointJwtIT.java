package security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.MainApplication;
import com.spring.security.AuthEntryPointJwt;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(classes = MainApplication.class)
@AutoConfigureMockMvc
public class AuthEntryPointJwtIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthEntryPointJwt authEntryPoint;

    @Test
    public void testCommence() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/test");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AuthenticationException authException = new UsernameNotFoundException("Invalid credentials");

        authEntryPoint.commence(request, response, authException);

        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());

        Map<String, Object> expectedBody = new HashMap<>();
        expectedBody.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        expectedBody.put("error", "Unauthorized");
        expectedBody.put("message", "Invalid credentials");
        expectedBody.put("path", "/api/test");
        ObjectMapper mapper = new ObjectMapper();
        assertEquals(mapper.writeValueAsString(expectedBody), response.getContentAsString());
    }

    @Test
    public void testCommenceWithUsernameNotFound() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/test");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AuthenticationException authException = new UsernameNotFoundException("User not found");

        authEntryPoint.commence(request, response, authException);
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());

        Map<String, Object> expectedBody = new HashMap<>();
        expectedBody.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        expectedBody.put("error", "Unauthorized");
        expectedBody.put("message", "User not found");
        expectedBody.put("path", "/api/test");
        ObjectMapper mapper = new ObjectMapper();
        assertEquals(mapper.writeValueAsString(expectedBody), response.getContentAsString());
    }
}