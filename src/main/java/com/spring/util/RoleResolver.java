package com.spring.util;

import com.spring.core.RoleRepository;
import com.spring.core.entities.ERole;
import com.spring.core.entities.Role;
import org.apache.commons.collections4.CollectionUtils;
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
        this.roleMap.put("anom", ERole.ROLE_ANONYMOUS);
        this.roleMap.put("user", ERole.ROLE_USER);
        this.roleMap.put("mod", ERole.ROLE_MODERATOR);
        this.roleMap.put("admin", ERole.ROLE_ADMIN);
    }

    public Set<Role> resolveRoles(Set<String> strRoles) {
        Set<Role> roles = new HashSet<>();

        if (CollectionUtils.isEmpty(strRoles)) {
            addDefaultUserRole(roles);
            return roles;
        }

        strRoles.forEach(strRole -> {
            ERole eRole = roleMap.entrySet().stream()
                    .filter(entry -> entry.getKey().equalsIgnoreCase(strRole))
                    .map(Map.Entry::getValue)
                    .findFirst()
                    .orElse(ERole.ROLE_USER);

            Role role = roleRepository.findByName(eRole)
                    .orElseGet(() -> roleRepository.findByName(ERole.ROLE_USER)
                            .orElseThrow(() -> new RuntimeException("Error: Default user role not found.")));
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