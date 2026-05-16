package com.example.petservice.auth;

import com.example.petservice.user.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;

public final class AuthDtos {
  private AuthDtos() {}

  public record RegisterRequest(
      @NotBlank @Size(max = 120) String fullName,
      @NotBlank @Email @Size(max = 160) String email,
      @NotBlank @Size(min = 8, max = 120) String password,
      @NotNull Role role
  ) {}

  public record LoginRequest(
      @NotBlank @Email String email,
      @NotBlank String password
  ) {}

  public record AuthResponse(String token, UserResponse user) {}

  public record UserResponse(
      Long id,
      String fullName,
      String email,
      Role role,
      OffsetDateTime createdAt
  ) {}
}
