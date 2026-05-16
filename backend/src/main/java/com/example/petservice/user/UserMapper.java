package com.example.petservice.user;

import com.example.petservice.user.UserDtos.StaffResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
  public StaffResponse toStaffResponse(User user) {
    return new StaffResponse(user.getId(), user.getFullName(), user.getEmail());
  }
}
