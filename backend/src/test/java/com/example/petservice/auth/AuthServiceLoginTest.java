package com.example.petservice.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.example.petservice.auth.AuthDtos.AuthResponse;
import com.example.petservice.auth.AuthDtos.LoginRequest;
import com.example.petservice.user.Role;
import com.example.petservice.user.User;
import com.example.petservice.user.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceLoginTest {
  @Mock
  private UserRepository users;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private JwtService jwtService;

  @Test
  void restoredUserCanLoginWhenRepositoryReturnsActiveUser() {
    AuthService service = new AuthService(users, passwordEncoder, jwtService);
    User user = new User();
    user.setId(12L);
    user.setFullName("Restored Customer");
    user.setEmail("restored.customer@example.com");
    user.setPasswordHash("encoded");
    user.setRole(Role.CUSTOMER);

    when(users.findByEmail("restored.customer@example.com")).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("Password123!", "encoded")).thenReturn(true);
    when(jwtService.generateToken(user)).thenReturn("token");

    AuthResponse response = service.login(new LoginRequest("restored.customer@example.com", "Password123!"));

    assertEquals("token", response.token());
    assertEquals(12L, response.user().id());
  }
}
