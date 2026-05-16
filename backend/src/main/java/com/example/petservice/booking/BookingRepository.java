package com.example.petservice.booking;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
  List<Booking> findByCustomerIdOrderByScheduledAtDesc(Long customerId);

  List<Booking> findByAssignedStaffIdOrderByScheduledAtDesc(Long staffId);

  List<Booking> findAllByOrderByScheduledAtDesc();
}
