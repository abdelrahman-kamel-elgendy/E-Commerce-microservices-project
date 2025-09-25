package com.e_Commerce.order_service.feigns;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.e_Commerce.order_service.dtos.response.CartResponse;

@FeignClient(name = "cart-service")
public interface CartServiceClient {

    @GetMapping("/api/carts/user")
    public ResponseEntity<CartResponse> getUserCart(
        @RequestParam Long userId
    );

    @DeleteMapping("api/carts/clear")
    void clearCart(
        @RequestParam Long cartId
    );
}
