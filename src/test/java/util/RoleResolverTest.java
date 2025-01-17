package util;

import com.spring.core.RoleRepository;
import com.spring.core.entities.ERole;
import com.spring.core.entities.Role;
import com.spring.util.RoleResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RoleResolverTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleResolver roleResolver;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testResolveRoles_NullOrEmptyRoles_ReturnsDefaultUserRole() {
        Role userRole = Role
                .builder()
                .name(ERole.ROLE_USER)
                .build();
        when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(Optional.of(userRole));

        Set<Role> resolvedRoles = roleResolver.resolveRoles(null);
        assertEquals(1, resolvedRoles.size());
        assertEquals(userRole, resolvedRoles.iterator().next());

        verify(roleRepository, times(1)).findByName(ERole.ROLE_USER);
    }

    @Test
    public void testResolveRoles_WithSpecificRoles() {
        Role adminRole = Role
                .builder()
                .name(ERole.ROLE_USER)
                .build();

        Role modRole = Role
                .builder()
                .name(ERole.ROLE_MODERATOR)
                .build();

        when(roleRepository.findByName(ERole.ROLE_ADMIN)).thenReturn(Optional.of(adminRole));
        when(roleRepository.findByName(ERole.ROLE_MODERATOR)).thenReturn(Optional.of(modRole));
        when(roleRepository.findByName(ERole.ROLE_USER))
                .thenReturn(Optional.of(Role
                        .builder()
                        .name(ERole.ROLE_USER)
                        .build()));

        Set<String> strRoles = Set.of("admin", "mod");
        Set<Role> resolvedRoles = roleResolver.resolveRoles(strRoles);

        assertEquals(2, resolvedRoles.size());
        assertEquals(Set.of(adminRole, modRole), resolvedRoles);

        verify(roleRepository, times(1)).findByName(ERole.ROLE_ADMIN);
        verify(roleRepository, times(1)).findByName(ERole.ROLE_MODERATOR);
    }

    @Test
    public void testResolveRoles_WithUnknownRole_DefaultsToUserRole() {
        Role userRole =  Role
                .builder()
                .name(ERole.ROLE_USER)
                .build();
        when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(Optional.of(userRole));

        Set<String> strRoles = Set.of("unknown");
        Set<Role> resolvedRoles = roleResolver.resolveRoles(strRoles);

        assertEquals(1, resolvedRoles.size());
        assertEquals(userRole, resolvedRoles.iterator().next());

        verify(roleRepository, times(1)).findByName(ERole.ROLE_USER);
    }
}
