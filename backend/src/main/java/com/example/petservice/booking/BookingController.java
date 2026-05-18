package com.example.petservice.booking;

import com.example.petservice.auth.AuthUserDetails;
import com.example.petservice.booking.BookingDtos.AssignBookingRequest;
import com.example.petservice.booking.BookingDtos.BookingResponse;
import com.example.petservice.booking.BookingDtos.CreateBookingRequest;
import com.example.petservice.booking.BookingDtos.UpdateBookingStatusRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
  private final BookingService bookingService;

  public BookingController(BookingService bookingService) {
    this.bookingService = bookingService;
  }

  @PostMapping
  BookingResponse create(@AuthenticationPrincipal AuthUserDetails user, @Valid @RequestBody CreateBookingRequest request) {
    return bookingService.create(user.currentUser(), request);
  }

  @GetMapping
  List<BookingResponse> list(@AuthenticationPrincipal AuthUserDetails user) {
    return bookingService.list(user.currentUser());
  }

  @GetMapping("/{id}")
  BookingResponse get(@AuthenticationPrincipal AuthUserDetails user, @PathVariable Long id) {
    return bookingService.get(user.currentUser(), id);
  }

  @PatchMapping("/{id}/assign")
  BookingResponse assign(
      @AuthenticationPrincipal AuthUserDetails user,
      @PathVariable Long id,
      @Valid @RequestBody AssignBookingRequest request
  ) {
    return bookingService.assign(user.currentUser(), id, request);
  }

  @PatchMapping("/{id}/status")
  BookingResponse updateStatus(
      @AuthenticationPrincipal AuthUserDetails user,
      @PathVariable Long id,
      @Valid @RequestBody UpdateBookingStatusRequest request
  ) {
    return bookingService.updateStatus(user.currentUser(), id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void delete(@AuthenticationPrincipal AuthUserDetails user, @PathVariable Long id) {
    bookingService.delete(user.currentUser(), id);
  }
}
