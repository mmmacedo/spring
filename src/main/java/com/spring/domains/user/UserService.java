package com.spring.domains.user;

import com.spring.util.RoleResolver;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final RoleResolver roleResolver;

    public UserService(UserRepository userRepository, PasswordEncoder encoder, RoleResolver roleResolver) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.roleResolver = roleResolver;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
    }

    public boolean deleteUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: "));
        user.delete();
        return user.getAudit().isDeleted();
    }

    public boolean changePassword(UUID userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: "));

        if (!encoder.matches(oldPassword, user.getPassword())) {
            return false;
        }

        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);
        return true;

    }

    public boolean updateRoles(UUID userId, Set<String> roles) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: "));
        user.setRoles(roleResolver.resolveRoles(roles));
        return true;
    }
}
