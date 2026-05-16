package com.example.petservice.user;

import com.example.petservice.user.UserDtos.StaffResponse;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/staff")
public class StaffController {
  private final StaffService staffService;

  public StaffController(StaffService staffService) {
    this.staffService = staffService;
  }

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  List<StaffResponse> listStaff() {
    return staffService.listStaff();
  }
}
