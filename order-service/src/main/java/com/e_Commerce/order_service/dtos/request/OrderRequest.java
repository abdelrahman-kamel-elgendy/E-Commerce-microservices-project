package com.e_Commerce.order_service.dtos.request;

import com.e_Commerce.order_service.models.ShippingAddress;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderRequest {
    private Long userId;
    private String cartId;
    private String customerEmail;
    private ShippingAddress shippingAddress;
}