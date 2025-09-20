package com.e_Commerce.order_service.dtos.response;

import java.time.LocalDateTime;

import com.e_Commerce.order_service.models.OrderStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderStatusHistory {
    private Long id;
    private OrderStatus status;
    private LocalDateTime changedAt;
    private String notes;
}
