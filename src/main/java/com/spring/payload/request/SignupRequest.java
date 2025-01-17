package com.spring.payload.request;

import com.spring.core.entities.ERole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SignupRequest {

    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    @Builder.Default
    private Set<String> role = Collections.singleton(ERole.ROLE_USER.name());

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

}