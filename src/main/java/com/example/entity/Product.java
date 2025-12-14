package com.example.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer quantity;

    private boolean deleted = false;

    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();
    private Boolean available;

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }
}
