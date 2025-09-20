package com.e_Commerce.cart_service.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InventoryReservationRequest {
    private Long productId;
    private Integer quantity;
    private String reservationId; 
}
