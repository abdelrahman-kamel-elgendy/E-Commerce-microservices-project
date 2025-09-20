package com.e_Commerce.order_service.dtos.request;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderItemRequest {
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
}
