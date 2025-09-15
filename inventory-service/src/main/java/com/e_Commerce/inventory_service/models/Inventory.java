package com.e_Commerce.inventory_service.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "Inventories")
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", updatable = false ,nullable = false)
    private Long productId;

    @Column(name = "quantity", updatable = false ,nullable = false)
    private int quantity;

    public Inventory(Long productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}
