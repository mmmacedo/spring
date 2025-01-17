package com.spring.core.services;

import com.spring.core.entities.Role;
import com.spring.domains.user.User;
import com.spring.payload.response.MessageResponse;
import com.spring.domains.user.UserRepository;
import com.spring.util.RoleResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
public class RegisterUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleResolver roleResolver;

    @Autowired
    PasswordEncoder encoder;

    public ResponseEntity<?> registerNewUser(String username, String password) {
        return registerNewUser(username, password, Set.of("ROLE_USER"));
    }

    public ResponseEntity<?> registerNewUser(String username, String password, Set<String> userRoles) {
        if (userRepository.existsByUsername(username)) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        User user = User.builder()
                .id(UUID.randomUUID())
                .username(username)
                .password(encoder.encode(password))
                .build();

        Set<Role> roles = roleResolver.resolveRoles(userRoles);
        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}
