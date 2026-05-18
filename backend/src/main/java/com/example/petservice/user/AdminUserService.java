package com.example.petservice.user;

import com.example.petservice.booking.BookingRepository;
import com.example.petservice.booking.BookingStatus;
import com.example.petservice.common.BadRequestException;
import com.example.petservice.common.NotFoundException;
import com.example.petservice.user.AdminUserDtos.AdminUserResponse;
import com.example.petservice.user.AdminUserDtos.CreateAdminUserRequest;
import com.example.petservice.user.AdminUserDtos.DeletedAdminUserResponse;
import com.example.petservice.user.AdminUserDtos.UpdateAdminUserRequest;
import java.util.Comparator;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminUserService {
  private final UserRepository users;
  private final BookingRepository bookings;
  private final PasswordEncoder passwordEncoder;
  private final JdbcTemplate jdbcTemplate;

  public AdminUserService(
      UserRepository users,
      BookingRepository bookings,
      PasswordEncoder passwordEncoder,
      JdbcTemplate jdbcTemplate
  ) {
    this.users = users;
    this.bookings = bookings;
    this.passwordEncoder = passwordEncoder;
    this.jdbcTemplate = jdbcTemplate;
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

  @Transactional(readOnly = true)
  public List<DeletedAdminUserResponse> listDeleted() {
    return jdbcTemplate.query(
        """
            select id, full_name, email, role, created_at, updated_at, deleted_at
            from users
            where deleted_at is not null
              and role in ('CUSTOMER', 'STAFF')
            order by deleted_at desc
            """,
        (rs, rowNum) -> new DeletedAdminUserResponse(
            rs.getLong("id"),
            rs.getString("full_name"),
            rs.getString("email"),
            Role.valueOf(rs.getString("role")),
            rs.getObject("created_at", java.time.OffsetDateTime.class),
            rs.getObject("updated_at", java.time.OffsetDateTime.class),
            rs.getObject("deleted_at", java.time.OffsetDateTime.class)
        )
    );
  }

  @Transactional(readOnly = true)
  public AdminUserResponse get(Long id) {
    return toResponse(findManagedUser(id));
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
    if (user.getRole() == Role.STAFF
        && bookings.existsByAssignedStaffIdAndStatusIn(id, List.of(BookingStatus.PENDING, BookingStatus.ASSIGNED))) {
      throw new BadRequestException("Cannot delete a staff member assigned to active bookings");
    }
    users.delete(user);
  }

  @Transactional
  public void restore(Long id) {
    int restoredRows = users.restoreManagedUserById(id);
    if (restoredRows == 0) {
      throw new NotFoundException("Deleted user not found");
    }
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
