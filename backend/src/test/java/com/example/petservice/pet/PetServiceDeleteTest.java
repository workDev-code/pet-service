package com.example.petservice.pet;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.petservice.auth.CurrentUser;
import com.example.petservice.booking.BookingRepository;
import com.example.petservice.user.Role;
import com.example.petservice.user.User;
import com.example.petservice.user.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PetServiceDeleteTest {
  @Mock
  private PetRepository pets;

  @Mock
  private UserRepository users;

  @Mock
  private BookingRepository bookings;

  @Mock
  private PetMapper mapper;

  @Mock
  private PetPhotoStorageService photoStorage;

  @Test
  void deleteSoftDeletesPetWithBookingHistory() {
    PetService service = new PetService(pets, users, bookings, mapper, photoStorage);
    Pet pet = petOwnedBy(10L, 1L);
    pet.setPhotoUrl("/uploads/pets/10.jpg");
    when(pets.findById(10L)).thenReturn(Optional.of(pet));
    when(bookings.existsByPetId(10L)).thenReturn(true);

    service.delete(new CurrentUser(1L, "customer@example.com", Role.CUSTOMER), 10L);

    verify(photoStorage).delete("/uploads/pets/10.jpg");
    verify(pets).delete(pet);
    verify(pets, never()).hardDeleteByIdWithoutBookings(10L);
  }

  @Test
  void deleteHardDeletesPetWithoutBookingHistory() {
    PetService service = new PetService(pets, users, bookings, mapper, photoStorage);
    Pet pet = petOwnedBy(11L, 1L);
    when(pets.findById(11L)).thenReturn(Optional.of(pet));
    when(bookings.existsByPetId(11L)).thenReturn(false);
    when(pets.hardDeleteByIdWithoutBookings(11L)).thenReturn(1);

    service.delete(new CurrentUser(1L, "customer@example.com", Role.CUSTOMER), 11L);

    verify(photoStorage).delete(null);
    verify(pets).hardDeleteByIdWithoutBookings(11L);
    verify(pets, never()).delete(pet);
  }

  private Pet petOwnedBy(Long petId, Long ownerId) {
    User owner = new User();
    owner.setId(ownerId);
    Pet pet = new Pet();
    pet.setId(petId);
    pet.setOwner(owner);
    return pet;
  }
}
