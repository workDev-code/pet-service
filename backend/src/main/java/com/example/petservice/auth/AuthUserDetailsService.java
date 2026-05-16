package com.example.petservice.auth;

import com.example.petservice.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthUserDetailsService implements UserDetailsService {
  private final UserRepository users;

  public AuthUserDetailsService(UserRepository users) {
    this.users = users;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return users.findByEmail(username)
        .map(AuthUserDetails::new)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
  }
}
