package com.spring.util;

import com.spring.entities.ERole;
import com.spring.entities.Role;
import com.spring.repositories.RoleRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class RoleResolver {

    private final RoleRepository roleRepository;
    private final Map<String, ERole> roleMap;

    public RoleResolver(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
        this.roleMap = new HashMap<>();
        this.roleMap.put("admin", ERole.ROLE_ADMIN);
        this.roleMap.put("anom", ERole.ROLE_ANONYMOUS);
        this.roleMap.put("mod", ERole.ROLE_MODERATOR);
        this.roleMap.put("user", ERole.ROLE_USER);
    }

    public Set<Role> resolveRoles(Set<String> strRoles) {
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            addDefaultUserRole(roles);
            return roles;
        }

        strRoles.forEach(strRole -> {
            ERole eRole = roleMap.getOrDefault(strRole, ERole.ROLE_USER);
            Role role = roleRepository.findByName(eRole)
                    .orElseThrow(() -> new RuntimeException("Error: Role " + eRole + " not found."));
            roles.add(role);
        });

        return roles;
    }

    private void addDefaultUserRole(Set<Role> roles) {
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Default user role not found."));
        roles.add(userRole);
    }
}