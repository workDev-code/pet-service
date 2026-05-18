package com.example.petservice.user;

import com.example.petservice.booking.BookingRepository;
import com.example.petservice.booking.BookingStatus;
import com.example.petservice.common.BadRequestException;
import com.example.petservice.common.NotFoundException;
import com.example.petservice.pet.PetRepository;
import com.example.petservice.user.AdminUserDtos.AdminUserResponse;
import com.example.petservice.user.AdminUserDtos.CreateAdminUserRequest;
import com.example.petservice.user.AdminUserDtos.UpdateAdminUserRequest;
import java.util.Comparator;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminUserService {
  private final UserRepository users;
  private final PetRepository pets;
  private final BookingRepository bookings;
  private final PasswordEncoder passwordEncoder;

  public AdminUserService(
      UserRepository users,
      PetRepository pets,
      BookingRepository bookings,
      PasswordEncoder passwordEncoder
  ) {
    this.users = users;
    this.pets = pets;
    this.bookings = bookings;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional(readOnly = true)
  public List<AdminUserResponse> list(Role role) {
    if (role != null) {
      requireManagedRole(role);
    }
    return users.findAll().stream()
        .filter(user -> role == null || user.getRole() == role)
        .sorted(Comparator.comparing(User::getCreatedAt).reversed())
        .map(this::toResponse)
        .toList();
  }

  @Transactional
  public AdminUserResponse create(CreateAdminUserRequest request) {
    requireManagedRole(request.role());
    String email = normalizeEmail(request.email());
    if (users.existsByEmail(email)) {
      throw new BadRequestException("Email is already in use");
    }

    User user = new User();
    user.setFullName(request.fullName());
    user.setEmail(email);
    user.setPasswordHash(passwordEncoder.encode(request.password()));
    user.setRole(request.role());
    return toResponse(users.save(user));
  }

  @Transactional
  public AdminUserResponse update(Long id, UpdateAdminUserRequest request) {
    requireManagedRole(request.role());
    User user = findManagedUser(id);
    String email = normalizeEmail(request.email());
    if (users.existsByEmailAndIdNot(email, id)) {
      throw new BadRequestException("Email is already in use");
    }

    user.setFullName(request.fullName());
    user.setEmail(email);
    user.setRole(request.role());
    if (request.password() != null && !request.password().isBlank()) {
      user.setPasswordHash(passwordEncoder.encode(request.password()));
    }
    return toResponse(user);
  }

  @Transactional
  public void delete(Long id) {
    User user = findManagedUser(id);
    if (user.getRole() == Role.CUSTOMER
        && (pets.existsByOwnerId(id) || bookings.existsByCustomerId(id))) {
      throw new BadRequestException("Cannot delete a customer with pets or bookings");
    }
    if (user.getRole() == Role.STAFF
        && bookings.existsByAssignedStaffIdAndStatusIn(id, List.of(BookingStatus.PENDING, BookingStatus.ASSIGNED))) {
      throw new BadRequestException("Cannot delete a staff member assigned to active bookings");
    }
    users.delete(user);
  }

  private User findManagedUser(Long id) {
    User user = users.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    requireManagedRole(user.getRole());
    return user;
  }

  private void requireManagedRole(Role role) {
    if (role != Role.CUSTOMER && role != Role.STAFF) {
      throw new BadRequestException("Admins can only manage customers and staff");
    }
  }

  private String normalizeEmail(String email) {
    return email.toLowerCase();
  }

  private AdminUserResponse toResponse(User user) {
    return new AdminUserResponse(
        user.getId(),
        user.getFullName(),
        user.getEmail(),
        user.getRole(),
        user.getCreatedAt()
    );
  }
}
