package com.e_Commerce.cart_service.models;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "cart")
@AllArgsConstructor
@NoArgsConstructor
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private CartStatus status;

    @Column(name = "item_count")
    private Integer itemCount = 0;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();
    


    public Cart(Long userId) {
        this.userId = userId;
        this.status = CartStatus.ACTIVE;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
    
    public void addItem(CartItem item) {
        items.add(item);
        item.setCart(this);
        this.itemCount = items.size();
        this.updatedAt = Instant.now();
    }
    
    public void removeItem(CartItem item) {
        items.remove(item);
        item.setCart(null);
        this.itemCount = items.size();
        this.updatedAt = Instant.now();
    }
}
