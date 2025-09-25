package com.e_Commerce.order_service.dtos.response;

import java.time.Instant;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Inventory {
    private Long id;
    private Long productId;
    private String skuCode;
    private Integer quantity;
    private Integer minStockLevel;
    private Integer maxStockLevel;
    private Integer reorderPoint;
    private String location;
    private String batchNumber;
    private Instant expiryDate;
    private Integer reservedQuantity;
    private Instant createdAt;
    private Instant updatedAt;
}
