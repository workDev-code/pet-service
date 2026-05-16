package com.example.petservice.user;

public final class UserDtos {
  private UserDtos() {}

  public record StaffResponse(Long id, String fullName, String email) {}
}
