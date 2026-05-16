package com.example.petservice.booking;

import com.example.petservice.booking.BookingDtos.BookingResponse;
import com.example.petservice.booking.BookingDtos.SimplePet;
import com.example.petservice.booking.BookingDtos.SimpleService;
import com.example.petservice.booking.BookingDtos.SimpleUser;
import com.example.petservice.pet.Pet;
import com.example.petservice.servicecatalog.ServiceCatalog;
import com.example.petservice.user.User;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {
  public BookingResponse toResponse(Booking booking) {
    return new BookingResponse(
        booking.getId(),
        toUser(booking.getCustomer()),
        toPet(booking.getPet()),
        toService(booking.getService()),
        toUser(booking.getAssignedStaff()),
        booking.getScheduledAt(),
        booking.getAddress(),
        booking.getNotes(),
        booking.getStatus(),
        booking.getCreatedAt(),
        booking.getUpdatedAt()
    );
  }

  private SimpleUser toUser(User user) {
    return user == null ? null : new SimpleUser(user.getId(), user.getFullName(), user.getEmail());
  }

  private SimplePet toPet(Pet pet) {
    return new SimplePet(pet.getId(), pet.getName(), pet.getSpecies(), pet.getBreed());
  }

  private SimpleService toService(ServiceCatalog service) {
    return new SimpleService(service.getId(), service.getName(), service.getPrice(), service.getDurationMinutes());
  }
}
