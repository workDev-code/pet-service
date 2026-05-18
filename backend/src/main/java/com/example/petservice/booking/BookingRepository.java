package com.example.petservice.booking;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
  List<Booking> findByCustomerIdOrderByScheduledAtDesc(Long customerId);

  List<Booking> findByAssignedStaffIdOrderByScheduledAtDesc(Long staffId);

  List<Booking> findAllByOrderByScheduledAtDesc();

  boolean existsByPetId(Long petId);

  boolean existsByCustomerId(Long customerId);

  boolean existsByAssignedStaffId(Long staffId);

  boolean existsByAssignedStaffIdAndStatusIn(Long staffId, Collection<BookingStatus> statuses);
}
