package com.example.petservice.user;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.petservice.booking.BookingRepository;
import com.example.petservice.booking.BookingStatus;
import com.example.petservice.common.BadRequestException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceDeleteTest {
  @Mock
  private UserRepository users;

  @Mock
  private BookingRepository bookings;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private JdbcTemplate jdbcTemplate;

  @Test
  void deleteAlwaysSoftDeletesCustomer() {
    AdminUserService service = new AdminUserService(users, bookings, passwordEncoder, jdbcTemplate);
    User customer = user(1L, Role.CUSTOMER);
    when(users.findById(1L)).thenReturn(Optional.of(customer));

    service.delete(1L);

    verify(users).delete(customer);
  }

  @Test
  void deleteRejectsStaffWithActiveBookings() {
    AdminUserService service = new AdminUserService(users, bookings, passwordEncoder, jdbcTemplate);
    User staff = user(2L, Role.STAFF);
    when(users.findById(2L)).thenReturn(Optional.of(staff));
    when(bookings.existsByAssignedStaffIdAndStatusIn(
        2L,
        List.of(BookingStatus.PENDING, BookingStatus.ASSIGNED)
    )).thenReturn(true);

    assertThrows(BadRequestException.class, () -> service.delete(2L));

    verify(users, never()).delete(staff);
  }

  private User user(Long id, Role role) {
    User user = new User();
    user.setId(id);
    user.setRole(role);
    return user;
  }
}
