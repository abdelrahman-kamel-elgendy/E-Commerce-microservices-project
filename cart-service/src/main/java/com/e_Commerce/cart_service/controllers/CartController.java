package com.e_Commerce.cart_service.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.e_Commerce.cart_service.dtos.CartResponse;
import com.e_Commerce.cart_service.feigns.ProductServiceClient;
import com.e_Commerce.cart_service.services.CartService;


@RestController
@RequestMapping("/api/carts")
public class CartController {
    @Autowired 
    CartService cartService;
    @Autowired
    ProductServiceClient productFeigns;

    @PostMapping("/add-to-cart")
    public ResponseEntity<CartResponse> addToCart(
        @RequestParam Long userId, 
        @RequestParam Long productId, 
        @RequestParam String sku, 
        @RequestParam int quantity
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.addToCart(userId, sku, productId, quantity));
    }

    @PutMapping("/items")
    public ResponseEntity<CartResponse> updateItemQuantity(
        @RequestParam Long cartId, 
        @RequestParam Long productId,
        @RequestParam String sku,
        @RequestParam int quantity
    ) {

        return ResponseEntity.ok(cartService.updateItemQuantity(cartId, productId, sku, quantity));
    }

    @DeleteMapping("/items")
    public ResponseEntity<CartResponse> removeItemFromCart(
        @RequestParam Long cartId, 
        @RequestParam Long productId
    ) {
        return ResponseEntity.ok(cartService.removeItemFromCart(cartId, productId));
    }
    
    @DeleteMapping("/clear")
    public ResponseEntity<CartResponse> clearCart(@RequestParam Long cartId) {
        return ResponseEntity.ok(cartService.clearCart(cartId));
    }
    
    @PostMapping("/order")
    public ResponseEntity<CartResponse> convertToOrder(@RequestParam Long cartId) {
        return ResponseEntity.ok(cartService.convertToOrder(cartId));
    }
    

    @GetMapping("/id")
    public ResponseEntity<CartResponse> getCart(@RequestParam Long cartId) {
        return ResponseEntity.ok(cartService.getCartResponseById(cartId));
    }

    @GetMapping("/user")
    public ResponseEntity<CartResponse> getUserCart(@RequestParam Long userId) {
        return ResponseEntity.ok(cartService.getCartResponseByUserId(userId));
    }
}
