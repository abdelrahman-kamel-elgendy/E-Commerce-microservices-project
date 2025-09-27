package com.e_Commerce.inventory_service.dto.response;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.e_Commerce.inventory_service.models.InventoryItem;
import com.e_Commerce.inventory_service.models.MovementType;
import com.e_Commerce.inventory_service.models.ReservationStatus;
import com.e_Commerce.inventory_service.models.StockMovement;
import com.e_Commerce.inventory_service.models.StockReservation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InventoryItemResponse {
    private Long id;
    private Long inventoryId;
    private String inventoryName;
    private String inventoryCode;
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

    private List<StockMovementResponse> movements = new ArrayList<>();
    private List<StockReservationResponse> reservations = new ArrayList<>();

    public InventoryItemResponse(InventoryItem item) {
        this.id = item.getId();
        this.inventoryId = item.getInventory().getId();
        this.inventoryName = item.getInventory().getName();
        this.inventoryCode = item.getInventory().getCode();
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

        this.movements = item.getMovements().stream()
                .map(movement -> new StockMovementResponse(movement))
                .collect(Collectors.toList());

        this.reservations = item.getReservations().stream()
                .map(reservation -> new StockReservationResponse(reservation))
                .collect(Collectors.toList());
    }

    @Getter
    @NoArgsConstructor
    private class StockMovementResponse {
        private Long id;
        private MovementType movementType;
        private Integer quantity;
        private String reason;
        private String referenceId;
        private Instant createdAt;
        private String createdBy;

        private StockMovementResponse(StockMovement movement) {
            this.id = movement.getId();
            this.movementType = movement.getMovementType();
            this.quantity = movement.getQuantity();
            this.reason = movement.getReason();
            this.referenceId = movement.getReferenceId();
            this.createdAt = movement.getCreatedAt();
            this.createdBy = movement.getCreatedBy();
        }
    }

    @Getter
    @NoArgsConstructor
    private class StockReservationResponse {
        private Long id;
        private Long orderId;
        private Integer quantity;
        private ReservationStatus status;
        private Instant createdAt;
        private Instant updatedAt;
        private Instant expiresAt;

        private StockReservationResponse(StockReservation reservation) {
            this.id = reservation.getId();
            this.orderId = reservation.getOrderId();
            this.quantity = reservation.getQuantity();
            this.status = reservation.getStatus();
            this.createdAt = reservation.getCreatedAt();
            this.updatedAt = reservation.getUpdatedAt();
            this.expiresAt = reservation.getExpiresAt();
        }
    }
}
