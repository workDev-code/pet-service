package com.example.petservice.auth;

import com.example.petservice.auth.AuthDtos.AuthResponse;
import com.example.petservice.auth.AuthDtos.LoginRequest;
import com.example.petservice.auth.AuthDtos.RegisterRequest;
import com.example.petservice.auth.AuthDtos.UserResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/register")
  AuthResponse register(@Valid @RequestBody RegisterRequest request) {
    return authService.register(request);
  }

  @PostMapping("/login")
  AuthResponse login(@Valid @RequestBody LoginRequest request) {
    return authService.login(request);
  }

  @GetMapping("/me")
  UserResponse me(@AuthenticationPrincipal AuthUserDetails userDetails) {
    return authService.me(userDetails.currentUser());
  }
}
