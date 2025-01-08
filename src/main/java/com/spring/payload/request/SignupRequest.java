package com.spring.payload.request;

import java.util.Set;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SignupRequest {

  @NotBlank
  @Size(min = 3, max = 20)
  private String username;

  private Set<String> role;

  @NotBlank
  @Size(min = 6, max = 40)
  private String password;

}
