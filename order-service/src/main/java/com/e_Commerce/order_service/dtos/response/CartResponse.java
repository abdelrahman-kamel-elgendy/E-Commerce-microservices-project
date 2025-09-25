package com.e_Commerce.order_service.dtos.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartResponse {
    private Long id;
    private Long userId;
    private BigDecimal totalAmount = BigDecimal.ZERO;
    private Integer itemCount;
    private Instant createdAt;
    private Instant updatedAt;
    private List<CartItemResponse> items;
}
