package com.spring.util;

import com.spring.exceptions.JwtParseFailException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Component
public class JwtParser {

    public String parseJwt(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("Authorization"))
                .filter(StringUtils::hasText)
                .filter(header -> header.startsWith("Bearer "))
                .map(header -> header.substring(7).trim())
                .filter(token -> !token.isEmpty())
                .orElseThrow(() -> new JwtParseFailException("Invalid or missing JWT token in Authorization header"));
    }

}
