package com.example.petservice.auth;

import com.example.petservice.user.Role;

public record CurrentUser(Long id, String email, Role role) {}
