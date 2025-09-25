package com.e_Commerce.inventory_service.dto.response;

import java.time.Instant;

import com.e_Commerce.inventory_service.models.InventoryItem;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class InventoryItemResponse {
    private Long id;
    private Long inventoryId;
    private String inventoryName;
    private String inventoryCode;
    private Long productId;
    private String sku;
    private Integer quantity;
    private Integer reservedQuantity;
    private Integer availableQuantity;
    private Integer minStockLevel;
    private Integer maxStockLevel;
    private Integer reorderPoint;
    private Instant expiryDate;
    private String location;
    private Boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;

    public InventoryItemResponse(InventoryItem item) {
        this.id = item.getId();
        this.inventoryId = item.getInventory().getId();
        this.inventoryName = item.getInventory().getName();
        this.inventoryCode = item.getInventory().getCode();
        this.productId = item.getProductId();
        this.sku = item.getSku();
        this.quantity = item.getQuantity();
        this.reservedQuantity = item.getReservedQuantity();
        this.availableQuantity = item.getAvailableQuantity();
        this.minStockLevel = item.getMinStockLevel();
        this.maxStockLevel = item.getMaxStockLevel();
        this.reorderPoint = item.getReorderPoint();
        this.expiryDate = item.getExpiryDate();
        this.location = item.getInventory().getAddress() + ", " + item.getInventory().getCity() + ", " + item
                .getInventory().getCountry();
        this.isActive = item.getActive();
        this.createdAt = item.getCreatedAt();
        this.updatedAt = item.getUpdatedAt();
    }
}
