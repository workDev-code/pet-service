package com.example.petservice.booking;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.petservice.auth.CurrentUser;
import com.example.petservice.pet.PetRepository;
import com.example.petservice.servicecatalog.ServiceCatalogRepository;
import com.example.petservice.user.Role;
import com.example.petservice.user.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookingServiceDeleteTest {
  @Mock
  private BookingRepository bookings;

  @Mock
  private UserRepository users;

  @Mock
  private PetRepository pets;

  @Mock
  private ServiceCatalogRepository services;

  @Mock
  private BookingMapper mapper;

  @Test
  void deleteSoftDeletesBooking() {
    BookingService service = new BookingService(bookings, users, pets, services, mapper);
    Booking booking = new Booking();
    booking.setId(5L);
    when(bookings.findById(5L)).thenReturn(Optional.of(booking));

    service.delete(new CurrentUser(1L, "admin@example.com", Role.ADMIN), 5L);

    verify(bookings).delete(booking);
  }
}
