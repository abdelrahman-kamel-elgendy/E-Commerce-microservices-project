package com.e_Commerce.cart_service.dtos;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CartItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal totalPrice;
    private String productImageUrl;

    public CartItemResponse(Long id, int quantity, ProductResponse product) {
        this.id = id;
        this.productId = product.getId(); 
        this.productName = product.getName();
        this.unitPrice = product.getPrice();
        this.quantity = quantity;
        this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        this.productImageUrl = product.getImages().getFirst().getImageUrl();
    }
}
