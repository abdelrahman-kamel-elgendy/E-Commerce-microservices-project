package com.e_Commerce.order_service.dtos.request;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderRequest {
    private Long userId;
    private String shippingAddress;
    private String billingAddress;
    private String customerEmail;
    private String customerPhone;
    private List<OrderItemRequest> items;
}