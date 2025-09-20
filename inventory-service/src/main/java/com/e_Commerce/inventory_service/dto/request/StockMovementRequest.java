package com.e_Commerce.inventory_service.dto.request;

import java.time.Instant;

import com.e_Commerce.inventory_service.models.MovementType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockMovementRequest {
    private Long id;
    private Long inventoryId;
    private MovementType movementType;
    private Integer quantity;
    private String reason;
    private String referenceId;
    private Instant createdAt;
    private String createdBy;
}
