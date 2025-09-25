package com.e_Commerce.cart_service.dtos;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.e_Commerce.cart_service.models.Cart;

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

    public CartResponse(Cart cart, List<CartItemResponse> items) {
        this.id = cart.getId();
        this.userId = cart.getUserId();
        this.items = items;
        this.itemCount = cart.getItemCount();
        this.createdAt = cart.getCreatedAt();
        this.updatedAt = cart.getUpdatedAt();

        for (CartItemResponse cartItemResponse : items)
            this.totalAmount = this.totalAmount.add(cartItemResponse.getTotalPrice());
    }
}
