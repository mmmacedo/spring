package com.spring.controllers;

import com.spring.payload.request.LoginRequest;
import com.spring.payload.request.SignupRequest;
import com.spring.services.AuthenticateUserService;
import com.spring.services.RegisterUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private RegisterUserService registerUserService;

    @Autowired
    private AuthenticateUserService authenticateUserService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return authenticateUserService.authenticateUser(loginRequest.getUsername(), loginRequest.getPassword());
    }

    @PostMapping("/signup")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<?> registerNewUser(@Valid @RequestBody SignupRequest signUpRequest) {
        return registerUserService.registerNewUser(
                signUpRequest.getUsername(),
                signUpRequest.getPassword());
    }

    @PostMapping("/newuser")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        return registerUserService.registerNewUser(
                signUpRequest.getUsername(),
                signUpRequest.getPassword(),
                signUpRequest.getRole());
    }
}