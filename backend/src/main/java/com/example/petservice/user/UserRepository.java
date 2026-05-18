package com.example.petservice.user;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);

  boolean existsByEmailAndIdNot(String email, Long id);

  List<User> findByRole(Role role);

  @Modifying
  @Query(
      value = """
          update users
          set deleted_at = null,
              updated_at = now()
          where id = :id
            and deleted_at is not null
            and role in ('CUSTOMER', 'STAFF')
          """,
      nativeQuery = true
  )
  int restoreManagedUserById(@Param("id") Long id);
}
