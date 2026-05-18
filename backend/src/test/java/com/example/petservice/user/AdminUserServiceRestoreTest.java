package com.example.petservice.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.petservice.booking.BookingRepository;
import com.example.petservice.common.NotFoundException;
import com.example.petservice.user.AdminUserDtos.DeletedAdminUserResponse;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceRestoreTest {
  @Mock
  private UserRepository users;

  @Mock
  private BookingRepository bookings;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private JdbcTemplate jdbcTemplate;

  @Test
  void listDeletedQueriesDeletedManagedUsers() {
    AdminUserService service = new AdminUserService(users, bookings, passwordEncoder, jdbcTemplate);
    when(jdbcTemplate.query(
        org.mockito.ArgumentMatchers.anyString(),
        org.mockito.ArgumentMatchers.<org.springframework.jdbc.core.RowMapper<DeletedAdminUserResponse>>any()
    )).thenReturn(List.of(new DeletedAdminUserResponse(
        7L,
        "Deleted Customer",
        "deleted.customer@example.com",
        Role.CUSTOMER,
        OffsetDateTime.now().minusDays(2),
        OffsetDateTime.now().minusDays(1),
        OffsetDateTime.now()
    )));

    List<DeletedAdminUserResponse> result = service.listDeleted();

    assertEquals(1, result.size());
    assertEquals(7L, result.getFirst().id());
    assertEquals(Role.CUSTOMER, result.getFirst().role());
  }

  @Test
  void restoreClearsDeletedFlagThroughRepository() {
    AdminUserService service = new AdminUserService(users, bookings, passwordEncoder, jdbcTemplate);
    when(users.restoreManagedUserById(7L)).thenReturn(1);

    service.restore(7L);

    verify(users).restoreManagedUserById(7L);
  }

  @Test
  void restoreReturnsNotFoundWhenNoDeletedManagedUserMatches() {
    AdminUserService service = new AdminUserService(users, bookings, passwordEncoder, jdbcTemplate);
    when(users.restoreManagedUserById(999L)).thenReturn(0);

    assertThrows(NotFoundException.class, () -> service.restore(999L));
  }

}
