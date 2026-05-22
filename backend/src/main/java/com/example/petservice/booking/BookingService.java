package com.example.petservice.booking;

import com.example.petservice.auth.CurrentUser;
import com.example.petservice.booking.BookingDtos.AssignBookingRequest;
import com.example.petservice.booking.BookingDtos.BookingResponse;
import com.example.petservice.booking.BookingDtos.CreateBookingRequest;
import com.example.petservice.booking.BookingDtos.UpdateBookingStatusRequest;
import com.example.petservice.common.BadRequestException;
import com.example.petservice.common.ForbiddenException;
import com.example.petservice.common.NotFoundException;
import com.example.petservice.pet.Pet;
import com.example.petservice.pet.PetNotFoundException;
import com.example.petservice.pet.PetRepository;
import com.example.petservice.servicecatalog.ServiceCatalog;
import com.example.petservice.servicecatalog.ServiceNotFoundException;
import com.example.petservice.servicecatalog.ServiceCatalogRepository;
import com.example.petservice.user.Role;
import com.example.petservice.user.User;
import com.example.petservice.user.UserRepository;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingService {
  private final BookingRepository bookings;
  private final UserRepository users;
  private final PetRepository pets;
  private final ServiceCatalogRepository services;
  private final BookingMapper mapper;

  public BookingService(
      BookingRepository bookings,
      UserRepository users,
      PetRepository pets,
      ServiceCatalogRepository services,
      BookingMapper mapper
  ) {
    this.bookings = bookings;
    this.users = users;
    this.pets = pets;
    this.services = services;
    this.mapper = mapper;
  }

  @Transactional
  public BookingResponse create(CurrentUser currentUser, CreateBookingRequest request) {
    if (currentUser.role() != Role.CUSTOMER) {
      throw new ForbiddenException("Only customers can create bookings");
    }
    User customer = users.findById(currentUser.id()).orElseThrow(() -> new NotFoundException("Customer not found"));
    Pet pet = pets.findById(request.petId()).orElseThrow(PetNotFoundException::new);
    if (!pet.getOwner().getId().equals(currentUser.id())) {
      throw new ForbiddenException("Cannot book for another customer's pet");
    }
    ServiceCatalog service = services.findById(request.serviceId())
        .filter(ServiceCatalog::isActive)
        .orElseThrow(ServiceNotFoundException::new);

    OffsetDateTime scheduledAt = parseScheduledAt(request.scheduledAt());
    requireFutureScheduledAt(scheduledAt);
    requireAvailableCreateSlot(pet.getId(), service.getId(), scheduledAt);

    OffsetDateTime now = OffsetDateTime.now();
    Booking booking = new Booking();
    booking.setCustomer(customer);
    booking.setPet(pet);
    booking.setService(service);
    booking.setScheduledAt(scheduledAt);
    booking.setAddress(request.address());
    booking.setNotes(request.notes());
    booking.setStatus(BookingStatus.PENDING);
    booking.setCreatedAt(now);
    booking.setUpdatedAt(now);
    try {
      return mapper.toResponse(bookings.saveAndFlush(booking));
    } catch (DataIntegrityViolationException ex) {
      throw translateBookingConstraint(ex);
    }
  }

  @Transactional(readOnly = true)
  public List<BookingResponse> list(CurrentUser currentUser) {
    List<Booking> visible = switch (currentUser.role()) {
      case ADMIN -> bookings.findAllByOrderByScheduledAtDesc();
      case STAFF -> bookings.findByAssignedStaffIdOrderByScheduledAtDesc(currentUser.id());
      case CUSTOMER -> bookings.findByCustomerIdOrderByScheduledAtDesc(currentUser.id());
    };
    return visible.stream().map(mapper::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public BookingResponse get(CurrentUser currentUser, Long id) {
    Booking booking = bookings.findById(id).orElseThrow(() -> new NotFoundException("Booking not found"));
    requireVisible(currentUser, booking);
    return mapper.toResponse(booking);
  }

  @Transactional
  public BookingResponse assign(CurrentUser currentUser, Long id, AssignBookingRequest request) {
    if (currentUser.role() != Role.ADMIN) {
      throw new ForbiddenException("Only admins can assign staff");
    }
    Booking booking = bookings.findById(id).orElseThrow(() -> new NotFoundException("Booking not found"));
    User staff = users.findById(request.staffId())
        .filter(user -> user.getRole() == Role.STAFF)
        .orElseThrow(() -> new BadRequestException("Staff user not found"));
    if (booking.getStatus() == BookingStatus.CANCELLED || booking.getStatus() == BookingStatus.COMPLETED) {
      throw new BadRequestException("Cannot assign a completed or cancelled booking");
    }
    if (bookings.existsByAssignedStaffIdAndScheduledAtAndStatusNotAndIdNot(
        staff.getId(),
        booking.getScheduledAt(),
        BookingStatus.CANCELLED,
        booking.getId()
    )) {
      throw new DuplicateStaffSlotException();
    }
    booking.setAssignedStaff(staff);
    booking.setStatus(BookingStatus.ASSIGNED);
    booking.setUpdatedAt(OffsetDateTime.now());
    try {
      return mapper.toResponse(bookings.saveAndFlush(booking));
    } catch (DataIntegrityViolationException ex) {
      throw translateBookingConstraint(ex);
    }
  }

  @Transactional
  public BookingResponse updateStatus(CurrentUser currentUser, Long id, UpdateBookingStatusRequest request) {
    Booking booking = bookings.findById(id).orElseThrow(() -> new NotFoundException("Booking not found"));
    if (currentUser.role() == Role.CUSTOMER) {
      if (!booking.getCustomer().getId().equals(currentUser.id()) || request.status() != BookingStatus.CANCELLED) {
        throw new ForbiddenException("Customers can only cancel their own bookings");
      }
    } else if (currentUser.role() == Role.STAFF) {
      if (booking.getAssignedStaff() == null || !booking.getAssignedStaff().getId().equals(currentUser.id())) {
        throw new ForbiddenException("Staff can only update assigned bookings");
      }
      if (request.status() != BookingStatus.COMPLETED && request.status() != BookingStatus.CANCELLED) {
        throw new BadRequestException("Staff can only complete or cancel assigned bookings");
      }
    }
    if (booking.getStatus() == request.status()) {
      return mapper.toResponse(booking);
    }
    booking.setStatus(request.status());
    booking.setUpdatedAt(OffsetDateTime.now());
    return mapper.toResponse(booking);
  }

  @Transactional
  public void delete(CurrentUser currentUser, Long id) {
    Booking booking = bookings.findById(id).orElseThrow(() -> new NotFoundException("Booking not found"));
    if (currentUser.role() != Role.ADMIN) {
      requireVisible(currentUser, booking);
    }
    bookings.delete(booking);
  }

  private void requireVisible(CurrentUser currentUser, Booking booking) {
    boolean visible = switch (currentUser.role()) {
      case ADMIN -> true;
      case STAFF -> booking.getAssignedStaff() != null
          && booking.getAssignedStaff().getId().equals(currentUser.id());
      case CUSTOMER -> booking.getCustomer().getId().equals(currentUser.id());
    };
    if (!visible) {
      throw new ForbiddenException("Booking is not visible to this user");
    }
  }

  private OffsetDateTime parseScheduledAt(String value) {
    try {
      return OffsetDateTime.parse(value);
    } catch (DateTimeParseException ignored) {
      try {
        return LocalDateTime.parse(value).atZone(ZoneId.systemDefault()).toOffsetDateTime();
      } catch (DateTimeParseException ex) {
        throw new InvalidScheduledAtException();
      }
    }
  }

  private void requireFutureScheduledAt(OffsetDateTime scheduledAt) {
    if (!scheduledAt.isAfter(OffsetDateTime.now())) {
      throw new InvalidScheduledAtException();
    }
  }

  private void requireAvailableCreateSlot(Long petId, Long serviceId, OffsetDateTime scheduledAt) {
    if (bookings.existsByPetIdAndScheduledAtAndStatusNot(petId, scheduledAt, BookingStatus.CANCELLED)) {
      throw new DuplicatePetBookingException();
    }
    if (bookings.existsByServiceIdAndScheduledAtAndStatusNot(serviceId, scheduledAt, BookingStatus.CANCELLED)) {
      throw new DuplicateServiceSlotException();
    }
  }

  private RuntimeException translateBookingConstraint(DataIntegrityViolationException ex) {
    String message = ex.getMostSpecificCause().getMessage();
    if (message != null) {
      if (message.contains("ux_bookings_pet_scheduled_active")) {
        return new DuplicatePetBookingException();
      }
      if (message.contains("ux_bookings_service_scheduled_active")) {
        return new DuplicateServiceSlotException();
      }
      if (message.contains("ux_bookings_staff_scheduled_active")) {
        return new DuplicateStaffSlotException();
      }
    }
    return ex;
  }
}
