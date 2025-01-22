package com.spring.domains.user;

import com.spring.core.services.RegisterUserService;
import com.spring.payload.request.SignupRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user")
@Transactional
public class UserController {

    @Autowired
    private RegisterUserService registerUserService;

    @Autowired
    private UserService userService;

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

    @DeleteMapping("/delete/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<?> deleteUser(@PathVariable UUID userId) {
        boolean deleted = userService.deleteUser(userId);
        if (deleted) {
            return ResponseEntity.ok("Usuário deletado com sucesso.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado.");
    }

//    @PutMapping("/change-password/{userId}")
//    @PreAuthorize("hasRole('ROLE_ADMIN') or #userId == authentication.principal.id")
//    public ResponseEntity<?> changePassword(@PathVariable Long userId, @RequestBody ChangePasswordRequest changePasswordRequest) {
//        boolean updated = registerUserService.changePassword(userId, changePasswordRequest.getOldPassword(), changePasswordRequest.getNewPassword());
//        if (updated) {
//            return ResponseEntity.ok("Senha alterada com sucesso.");
//        }
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Falha ao alterar a senha.");
//    }
//
//    @PutMapping("/change-roles/{userId}")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
//    public ResponseEntity<?> changeRoles(@PathVariable Long userId, @RequestBody UpdateRolesRequest updateRolesRequest) {
//        boolean updated = registerUserService.updateRoles(userId, updateRolesRequest.getRoles());
//        if (updated) {
//            return ResponseEntity.ok("Funções do usuário alteradas com sucesso.");
//        }
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Falha ao alterar as funções.");
//    }

}
