package com.e_Commerce.inventory_service.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateInventoryItem {
    @Min(value = 0, message = "Quantity must be zero or positive")
    private Integer quantity;

    @Min(value = 0, message = "Min stock level must be zero or positive")
    private Integer minStockLevel;

    @Min(value = 1, message = "Max stock level must be positive")
    private Integer maxStockLevel;

    @Min(value = 0, message = "Reorder point must be zero or positive")
    private Integer reorderPoint;

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

    @AssertTrue(message = "Quantity must not exceed max stock level")
    public boolean isQuantityWithinMaxLimit() {
        if (maxStockLevel == null || quantity == null)
            return true;
        return quantity <= maxStockLevel;
    }
}
