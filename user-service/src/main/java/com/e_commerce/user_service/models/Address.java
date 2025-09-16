package com.e_commerce.user_service.models;

import java.time.Instant;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "addresses")
public class Address {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @JoinColumn(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "street", nullable = false, length = 255)
    private String street;
    
    @Column(name = "city", nullable = false, length = 100)
    private String city;
    
    @Column(name = "country", nullable = false, length = 100)
    private String country;
    
    @Column(name = "postal_code", nullable = true, length = 20)
    private String postalCode;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Instant updatedAt;


    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
