package com.example.petservice.pet;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PetRepository extends JpaRepository<Pet, Long> {
  List<Pet> findByOwnerId(Long ownerId);

  boolean existsByOwnerId(Long ownerId);

  @Modifying
  @Query(
      value = """
          delete from pets
          where id = :id
            and not exists (
              select 1 from bookings where bookings.pet_id = pets.id
            )
          """,
      nativeQuery = true
  )
  int hardDeleteByIdWithoutBookings(@Param("id") Long id);
}
