package com.e_Commerce.inventory_service.dto.request;

import java.time.Instant;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InventoryRequest {
    @NotNull(message = "productId is required")
    private Long productId;

    @NotBlank(message = "skuCode is required")
    private String skuCode;

    @NotNull(message = "quantity is required")
    @Min(value = 1, message = "quantity must be positive")
    private Integer quantity;

    @Min(value = 1, message = "minStockLevel must be positive")
    private Integer minStockLevel;

    @Min(value = 1, message = "maxStockLevel must be positive")
    private Integer maxStockLevel;

    @Min(value = 1, message = "reorderPoint must be positive")
    private Integer reorderPoint;

    private String location;
    private String batchNumber;

    @Future(message = "expiryDate must be in the future")
    private Instant expiryDate;

    @Min(value = 0, message = "reservedQuantity must be zero or positive")
    private Integer reservedQuantity;

    @AssertTrue(message = "maxStockLevel must be greater than or equal to minStockLevel when both are provided")
    public boolean isMaxGreaterOrEqualMin() {
        if (minStockLevel == null || maxStockLevel == null) return true;
        return maxStockLevel >= minStockLevel;
    }

    @AssertTrue(message = "reorderPoint must be between minStockLevel and maxStockLevel when min/max are provided")
    public boolean isReorderPointValid() {
        if (reorderPoint == null) return true;
        if (minStockLevel != null && reorderPoint < minStockLevel) return false;
        if (maxStockLevel != null && reorderPoint > maxStockLevel) return false;
        return true;
    }

}
