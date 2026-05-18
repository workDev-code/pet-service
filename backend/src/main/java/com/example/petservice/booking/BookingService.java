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
import com.example.petservice.pet.PetRepository;
import com.example.petservice.servicecatalog.ServiceCatalog;
import com.example.petservice.servicecatalog.ServiceCatalogRepository;
import com.example.petservice.user.Role;
import com.example.petservice.user.User;
import com.example.petservice.user.UserRepository;
import java.time.OffsetDateTime;
import java.util.List;
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
    Pet pet = pets.findById(request.petId()).orElseThrow(() -> new NotFoundException("Pet not found"));
    if (!pet.getOwner().getId().equals(currentUser.id())) {
      throw new ForbiddenException("Cannot book for another customer's pet");
    }
    ServiceCatalog service = services.findById(request.serviceId())
        .filter(ServiceCatalog::isActive)
        .orElseThrow(() -> new NotFoundException("Active service not found"));

    OffsetDateTime now = OffsetDateTime.now();
    Booking booking = new Booking();
    booking.setCustomer(customer);
    booking.setPet(pet);
    booking.setService(service);
    booking.setScheduledAt(request.scheduledAt());
    booking.setAddress(request.address());
    booking.setNotes(request.notes());
    booking.setStatus(BookingStatus.PENDING);
    booking.setCreatedAt(now);
    booking.setUpdatedAt(now);
    return mapper.toResponse(bookings.save(booking));
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
    booking.setAssignedStaff(staff);
    booking.setStatus(BookingStatus.ASSIGNED);
    booking.setUpdatedAt(OffsetDateTime.now());
    return mapper.toResponse(booking);
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
}
