package com.e_Commerce.inventory_service.dto.request;

import java.time.Instant;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InventoryItemRequest {
    @NotNull(message = "Inventory ID is required")
    @Min(value = 1, message = "Inventory ID must be positive")
    private Long inventoryId;

    @NotBlank(message = "SKU is required")
    @Pattern(regexp = "^[A-Z0-9-]+$", message = "SKU must contain only uppercase letters, numbers, and hyphens")
    private String sku;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be positive")
    private Integer quantity;

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

    @AssertTrue(message = "Quantity must not exceed max stock level")
    public boolean isQuantityWithinMaxLimit() {
        if (maxStockLevel == null || quantity == null)
            return true;
        return quantity <= maxStockLevel;
    }
}
