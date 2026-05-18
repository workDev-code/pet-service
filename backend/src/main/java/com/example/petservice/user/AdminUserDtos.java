package com.example.petservice.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;

public final class AdminUserDtos {
  private AdminUserDtos() {}

  public record AdminUserResponse(
      Long id,
      String fullName,
      String email,
      Role role,
      OffsetDateTime createdAt
  ) {}

  public record DeletedAdminUserResponse(
      Long id,
      String fullName,
      String email,
      Role role,
      OffsetDateTime createdAt,
      OffsetDateTime updatedAt,
      OffsetDateTime deletedAt
  ) {}

  public record CreateAdminUserRequest(
      @NotBlank @Size(max = 120) String fullName,
      @NotBlank @Email @Size(max = 160) String email,
      @NotBlank @Size(min = 8, max = 120) String password,
      @NotNull Role role
  ) {}

  public record UpdateAdminUserRequest(
      @NotBlank @Size(max = 120) String fullName,
      @NotBlank @Email @Size(max = 160) String email,
      @Size(min = 8, max = 120) String password,
      @NotNull Role role
  ) {}
}
