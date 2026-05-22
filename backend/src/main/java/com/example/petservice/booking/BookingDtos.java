package com.example.petservice.booking;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public final class BookingDtos {
  private BookingDtos() {}

  public record CreateBookingRequest(
      @NotNull Long petId,
      @NotNull Long serviceId,
      @NotBlank String scheduledAt,
      @NotBlank @Size(max = 1000) String address,
      @Size(max = 1000) String notes
  ) {}

  public record AssignBookingRequest(@NotNull Long staffId) {}

  public record UpdateBookingStatusRequest(@NotNull BookingStatus status) {}

  public record BookingResponse(
      Long id,
      SimpleUser customer,
      SimplePet pet,
      SimpleService service,
      SimpleUser assignedStaff,
      OffsetDateTime scheduledAt,
      String address,
      String notes,
      BookingStatus status,
      OffsetDateTime createdAt,
      OffsetDateTime updatedAt
  ) {}

  public record SimpleUser(Long id, String fullName, String email) {}

  public record SimplePet(Long id, String name, String species, String breed) {}

  public record SimpleService(Long id, String name, BigDecimal price, Integer durationMinutes) {}
}
