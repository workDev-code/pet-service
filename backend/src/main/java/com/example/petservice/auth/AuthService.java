package com.example.petservice.auth;

import com.example.petservice.auth.AuthDtos.AuthResponse;
import com.example.petservice.auth.AuthDtos.LoginRequest;
import com.example.petservice.auth.AuthDtos.RegisterRequest;
import com.example.petservice.auth.AuthDtos.UserResponse;
import com.example.petservice.common.BadRequestException;
import com.example.petservice.common.NotFoundException;
import com.example.petservice.user.Role;
import com.example.petservice.user.User;
import com.example.petservice.user.UserRepository;
import java.time.OffsetDateTime;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
  private final UserRepository users;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  public AuthService(UserRepository users, PasswordEncoder passwordEncoder, JwtService jwtService) {
    this.users = users;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
  }

  @Transactional
  public AuthResponse register(RegisterRequest request) {
    if (request.role() == Role.ADMIN) {
      throw new BadRequestException("Admin users cannot self-register");
    }
    if (users.existsByEmail(request.email().toLowerCase())) {
      throw new BadRequestException("Email already registered");
    }

    User user = new User();
    user.setFullName(request.fullName());
    user.setEmail(request.email().toLowerCase());
    user.setPasswordHash(passwordEncoder.encode(request.password()));
    user.setRole(request.role());
    user.setCreatedAt(OffsetDateTime.now());
    users.save(user);

    return new AuthResponse(jwtService.generateToken(user), toResponse(user));
  }

  @Transactional(readOnly = true)
  public AuthResponse login(LoginRequest request) {
    User user = users.findByEmail(request.email().toLowerCase())
        .orElseThrow(() -> new BadRequestException("Invalid email or password"));
    if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
      throw new BadRequestException("Invalid email or password");
    }
    return new AuthResponse(jwtService.generateToken(user), toResponse(user));
  }

  @Transactional(readOnly = true)
  public UserResponse me(CurrentUser currentUser) {
    User user = users.findById(currentUser.id()).orElseThrow(() -> new NotFoundException("User not found"));
    return toResponse(user);
  }

  public UserResponse toResponse(User user) {
    return new UserResponse(user.getId(), user.getFullName(), user.getEmail(), user.getRole(), user.getCreatedAt());
  }
}
