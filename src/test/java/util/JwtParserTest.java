package util;

import com.spring.exceptions.JwtParseFailException;
import com.spring.util.JwtParser;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class JwtParserTest {

    private final JwtParser jwtParser = new JwtParser();

    @Test
    public void testParseJwt_ValidToken_ReturnsToken() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer validToken123");

        String result = jwtParser.parseJwt(request);
        assertEquals("validToken123", result, "Token should be extracted correctly");
    }

    @Test
    public void testParseJwt_MissingAuthorizationHeader_ThrowsException() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn(null);

        JwtParseFailException exception = assertThrows(JwtParseFailException.class, () -> jwtParser.parseJwt(request));
        assertEquals("Invalid or missing JWT token in Authorization header", exception.getMessage());
    }

    @Test
    public void testParseJwt_InvalidTokenFormat_ThrowsException() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("InvalidToken");

        JwtParseFailException exception = assertThrows(JwtParseFailException.class, () -> jwtParser.parseJwt(request));
        assertEquals("Invalid or missing JWT token in Authorization header", exception.getMessage());
    }

    @Test
    public void testParseJwt_BearerWithoutToken_ThrowsException() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer ");

        JwtParseFailException exception = assertThrows(JwtParseFailException.class, () -> jwtParser.parseJwt(request));
        assertEquals("Invalid or missing JWT token in Authorization header", exception.getMessage());
    }
}