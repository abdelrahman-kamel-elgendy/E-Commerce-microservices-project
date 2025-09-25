package com.e_Commerce.inventory_service.dto.response;

import java.time.Instant;

import com.e_Commerce.inventory_service.models.Inventory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class InventoryResponse {
    private Long id;
    private String name;
    private String code;
    private String address;
    private String city;
    private String postalCode;
    private String country;
    private Boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;

    public InventoryResponse(Inventory inventory) {
        this.id = inventory.getId();
        this.name = inventory.getName();
        this.code = inventory.getCode();
        this.address = inventory.getAddress();
        this.city = inventory.getCity();
        this.postalCode = inventory.getPostalCode();
        this.country = inventory.getCountry();
        this.isActive = inventory.getActive();
        this.createdAt = inventory.getCreatedAt();
        this.updatedAt = inventory.getUpdatedAt();
    }
}
