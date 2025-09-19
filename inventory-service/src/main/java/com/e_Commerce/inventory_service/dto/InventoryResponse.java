package com.e_Commerce.inventory_service.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.e_Commerce.inventory_service.models.Inventory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponse {
    private Long id;
    private Long productId;
    private String skuCode;
    private String productName;
    private String image;
    private String productDescription;
    private BigDecimal productPrice;
    private String categoryName;
    private String brandName;
    private Integer quantity;
    private Integer reservedQuantity;
    private Integer minStockLevel;
    private Integer maxStockLevel;
    private Integer reorderPoint;
    private String location;
    private String batchNumber;
    private Instant expiryDate;
    private Instant createdAt;
    private Instant updatedAt;

    public InventoryResponse(Inventory inventory, ProductResponse product) {
        this.id = inventory.getId();
        this.productId = inventory.getProductId();
        this.skuCode = inventory.getSkuCode();
        this.productName = product.getName();
        this.productDescription = product.getDescription();
        this.productPrice = product.getPrice();
        this.categoryName = product.getCategoryName();
        this.brandName = product.getBrandName();
        this.image = product.getImages().getFirst().getImageUrl();
        this.quantity = inventory.getQuantity();
        this.reservedQuantity = inventory.getReservedQuantity();
        this.minStockLevel = inventory.getMinStockLevel();
        this.maxStockLevel = inventory.getMaxStockLevel();
        this.reorderPoint = inventory.getReorderPoint();
        this.location = inventory.getLocation();
        this.batchNumber = inventory.getBatchNumber();
        this.expiryDate = inventory.getExpiryDate();
        this.createdAt = inventory.getCreatedAt();
        this.updatedAt = inventory.getUpdatedAt();
    }
}
