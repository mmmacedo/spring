package com.spring.core;

import com.spring.core.services.AuthenticateUserService;
import com.spring.payload.request.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticateUserService authenticateUserService;

    public AuthController(AuthenticateUserService authenticateUserService) {
        this.authenticateUserService = authenticateUserService;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return authenticateUserService.authenticateUser(loginRequest.getUsername(), loginRequest.getPassword());
    }
}