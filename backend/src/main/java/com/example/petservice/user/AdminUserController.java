package com.example.petservice.user;

import com.example.petservice.user.AdminUserDtos.AdminUserResponse;
import com.example.petservice.user.AdminUserDtos.CreateAdminUserRequest;
import com.example.petservice.user.AdminUserDtos.UpdateAdminUserRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {
  private final AdminUserService adminUserService;

  public AdminUserController(AdminUserService adminUserService) {
    this.adminUserService = adminUserService;
  }

  @GetMapping
  List<AdminUserResponse> list(@RequestParam(required = false) Role role) {
    return adminUserService.list(role);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  AdminUserResponse create(@Valid @RequestBody CreateAdminUserRequest request) {
    return adminUserService.create(request);
  }

  @PutMapping("/{id}")
  AdminUserResponse update(@PathVariable Long id, @Valid @RequestBody UpdateAdminUserRequest request) {
    return adminUserService.update(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void delete(@PathVariable Long id) {
    adminUserService.delete(id);
  }
}
