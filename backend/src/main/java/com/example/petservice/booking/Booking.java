package com.example.petservice.booking;

import com.example.petservice.common.AuditableEntity;
import com.example.petservice.pet.Pet;
import com.example.petservice.servicecatalog.ServiceCatalog;
import com.example.petservice.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Getter
@Setter
@Entity
@Table(name = "bookings")
@SQLDelete(sql = "update bookings set deleted_at = now(), updated_at = now() where id = ?")
@Where(clause = "deleted_at is null")
public class Booking extends AuditableEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "customer_id")
  private User customer;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "pet_id")
  private Pet pet;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "service_id")
  private ServiceCatalog service;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "assigned_staff_id")
  private User assignedStaff;

  @Column(nullable = false)
  private OffsetDateTime scheduledAt;

  @Column(nullable = false, columnDefinition = "text")
  private String address;

  @Column(columnDefinition = "text")
  private String notes;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private BookingStatus status;
}
