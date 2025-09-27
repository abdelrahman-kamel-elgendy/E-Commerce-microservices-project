package com.e_Commerce.inventory_service.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ReservationRequest {
    @NotNull(message = "SKU is required")
    private String sku;

    @Positive(message = "Quantity must be positive")
    private Integer quantity;

    @NotNull(message = "Order ID is required")
    private Long orderId;
}
