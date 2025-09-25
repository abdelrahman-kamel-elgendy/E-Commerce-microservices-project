package com.e_Commerce.inventory_service.dto.request;

import java.time.Instant;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateInventoryItemRequest {
    @Min(value = 0, message = "Min stock level must be zero or positive")
    private Integer minStockLevel;

    @Min(value = 1, message = "Max stock level must be positive")
    private Integer maxStockLevel;

    @Min(value = 0, message = "Reorder point must be zero or positive")
    private Integer reorderPoint;

    @Future(message = "Expiry date must be in the future")
    private Instant expiryDate;

    @AssertTrue(message = "Max stock level must be greater than or equal to min stock level")
    public boolean isMaxGreaterOrEqualMin() {
        if (maxStockLevel == null || minStockLevel == null)
            return true;
        return maxStockLevel >= minStockLevel;
    }

    @AssertTrue(message = "Reorder point must be between min and max stock levels")
    public boolean isReorderPointValid() {
        if (reorderPoint == null)
            return true;
        if (minStockLevel != null && reorderPoint < minStockLevel)
            return false;
        if (maxStockLevel != null && reorderPoint > maxStockLevel)
            return false;
        return true;
    }
}
