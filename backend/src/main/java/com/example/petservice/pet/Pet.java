package com.example.petservice.pet;

import com.example.petservice.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "pets")
public class Pet {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "owner_id")
  private User owner;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String species;

  private String breed;

  @Column(nullable = false, precision = 5, scale = 2)
  private BigDecimal weightKg;

  @Column(columnDefinition = "text")
  private String notes;

  @Column(columnDefinition = "text")
  private String photoUrl;
}
