package com.e_Commerce.inventory_service.dto.response;

import java.time.Instant;

import com.e_Commerce.inventory_service.models.ReservationStatus;
import com.e_Commerce.inventory_service.models.StockReservation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StockReservationResponse {
    private Long id;
    private Long inventoryItemId;
    private Long orderId;
    private Integer quantity;
    private ReservationStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant expiresAt;

    public StockReservationResponse(StockReservation reservation) {
        this.id = reservation.getId();
        this.inventoryItemId = reservation.getInventoryItem().getId();
        this.orderId = reservation.getOrderId();
        this.quantity = reservation.getQuantity();
        this.status = reservation.getStatus();
        this.createdAt = reservation.getCreatedAt();
        this.updatedAt = reservation.getUpdatedAt();
        this.expiresAt = reservation.getExpiresAt();
    }
}
