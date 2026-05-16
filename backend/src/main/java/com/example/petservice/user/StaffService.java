package com.example.petservice.user;

import com.example.petservice.user.UserDtos.StaffResponse;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StaffService {
  private final UserRepository users;
  private final UserMapper mapper;

  public StaffService(UserRepository users, UserMapper mapper) {
    this.users = users;
    this.mapper = mapper;
  }

  @Transactional(readOnly = true)
  public List<StaffResponse> listStaff() {
    return users.findByRole(Role.STAFF).stream().map(mapper::toStaffResponse).toList();
  }
}
