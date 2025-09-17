package com.e_Commerce.cart_service.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.e_Commerce.cart_service.dtos.CartDTO;
import com.e_Commerce.cart_service.dtos.CartItemDTO;
import com.e_Commerce.cart_service.dtos.ProductDetails;
import com.e_Commerce.cart_service.feigns.ProductFeigns;
import com.e_Commerce.cart_service.models.Cart;
import com.e_Commerce.cart_service.models.CartItem;
import com.e_Commerce.cart_service.res.ApiResponse;
import com.e_Commerce.cart_service.services.CartService;


@RestController
@RequestMapping("/api/carts")
public class CartController {
    @Autowired 
    CartService cartService;
    @Autowired
    ProductFeigns productFeigns;

    @PostMapping
    public ResponseEntity<ApiResponse<CartDTO>> createOrGetCart(@RequestParam Long userId) {
        Cart cart = cartService.getOrCreateCart(userId);
        List<CartItemDTO> items = new ArrayList<>();
        for (CartItem cartItem : cartService.getItemByCartId(cart.getId())) {
            ProductDetails product = productFeigns.getProductById(cartItem.getId()).getBody().getData();
            items.add(
                new CartItemDTO(
                    cartItem.getId(), 
                    cartItem.getQuantity(),
                    product                 
                )
            );
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(
            new ApiResponse<CartDTO>(
                true,
                "Cart retrieved successfully",
                new CartDTO(
                    cart, 
                    items
                )
            )
        );
    }

    @PostMapping("/add-to-cart")
    public ResponseEntity<ApiResponse<CartDTO>> addItemToCart(@RequestParam Long cartId, @RequestParam Long productId, @RequestParam int quantity) {
        Cart cart = cartService.addItemToCart(cartId, productId, quantity);
        List<CartItemDTO> items = new ArrayList<>();
        for (CartItem cartItem : cartService.getItemByCartId(cart.getId())) {
            ProductDetails product = productFeigns.getProductById(cartItem.getId()).getBody().getData();
            items.add(
                new CartItemDTO(
                    cartItem.getId(), 
                    cartItem.getQuantity(),
                    product                 
                )
            );
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(
            new ApiResponse<CartDTO>(
                true,
                "Item added successfully",
                new CartDTO(
                    cart, 
                    items
                )
            )
        );
    }

    @PutMapping("/items")
    public ResponseEntity<ApiResponse<CartDTO>> updateItemQuantity(@RequestParam Long cartId, @RequestParam Long productId, @RequestParam int quantity) {
        Cart cart = cartService.updateItemQuantity(cartId, productId, quantity);
        
        List<CartItemDTO> items = new ArrayList<>();
        for (CartItem cartItem : cartService.getItemByCartId(cart.getId())) {
            ProductDetails product = productFeigns.getProductById(cartItem.getId()).getBody().getData();
            items.add(
                new CartItemDTO(
                    cartItem.getId(), 
                    cartItem.getQuantity(),
                    product                 
                )
            );
        }

        return ResponseEntity.ok(
            new ApiResponse<CartDTO>(
                true,
                "Item updated successfully",
                new CartDTO(
                    cart, 
                    items
                )
            )
        );
    }

    @DeleteMapping("/items")
    public ResponseEntity<ApiResponse<CartDTO>> removeItemFromCart(@RequestParam Long cartId, @RequestParam Long productId) {
        Cart cart = cartService.removeItemFromCart(cartId, productId);
        
        List<CartItemDTO> items = new ArrayList<>();
        for (CartItem cartItem : cartService.getItemByCartId(cart.getId())) {
            ProductDetails product = productFeigns.getProductById(cartItem.getId()).getBody().getData();
            items.add(
                new CartItemDTO(
                    cartItem.getId(), 
                    cartItem.getQuantity(),
                    product                 
                )
            );
        }

        return ResponseEntity.ok(
            new ApiResponse<CartDTO>(
                true,
                "Item removed successfully",
                new CartDTO(
                    cart, 
                    items
                )
            )
        );
    }
    
    @DeleteMapping("/cart")
    public ResponseEntity<ApiResponse<CartDTO>> clearCart(@RequestParam Long cartId) {
        Cart cart = cartService.clearCart(cartId);

        return ResponseEntity.ok(
            new ApiResponse<CartDTO>(
                true,
                "Cart cleared successfully",
                new CartDTO(
                    cart, 
                    new ArrayList<>()
                )
            )
        );
    }
    
    @PostMapping("/order")
    public ResponseEntity<ApiResponse<CartDTO>> convertToOrder(@RequestParam Long cartId) {
        Cart cart = cartService.convertToOrder(cartId);
        
        List<CartItemDTO> items = new ArrayList<>();
        for (CartItem cartItem : cartService.getItemByCartId(cart.getId())) {
            ProductDetails product = productFeigns.getProductById(cartItem.getId()).getBody().getData();
            items.add(
                new CartItemDTO(
                    cartItem.getId(), 
                    cartItem.getQuantity(),
                    product                 
                )
            );
        }

        return ResponseEntity.ok(
            new ApiResponse<CartDTO>(
                true,
                "Cart converted to order successfully",
                new CartDTO(
                    cart, 
                    items
                )
            )
        );
    }
    
    @PostMapping("/abandon")
    public ResponseEntity<ApiResponse<CartDTO>> abandonCart(@RequestParam Long cartId) {
        Cart cart = cartService.abandonCart(cartId);
        
        List<CartItemDTO> items = new ArrayList<>();
        for (CartItem cartItem : cartService.getItemByCartId(cart.getId())) {
            ProductDetails product = productFeigns.getProductById(cartItem.getId()).getBody().getData();
            items.add(
                new CartItemDTO(
                    cartItem.getId(), 
                    cartItem.getQuantity(),
                    product                 
                )
            );
        }

        return ResponseEntity.ok(
            new ApiResponse<CartDTO>(
                true,
                "Cart converted to abandon successfully",
                new CartDTO(
                    cart, 
                    items
                )
            )
        );
    }


    @GetMapping("/id")
    public ResponseEntity<ApiResponse<CartDTO>> getCart(@RequestParam Long cartId) {
        Cart cart = cartService.getCartById(cartId);
        List<CartItemDTO> items = new ArrayList<>();
        for (CartItem cartItem : cartService.getItemByCartId(cart.getId())) {
            ProductDetails product = productFeigns.getProductById(cartItem.getId()).getBody().getData();
            items.add(
                new CartItemDTO(
                    cartItem.getId(), 
                    cartItem.getQuantity(),
                    product                 
                )
            );
        }

        return ResponseEntity.ok(
            new ApiResponse<CartDTO>(
                true,
                "Cart retrieved successfully",
                new CartDTO(
                    cart, 
                    items
                )
            )
        );
    }

    @GetMapping("/user")
    public ResponseEntity<ApiResponse<CartDTO>> getUserCart(@RequestParam Long userId) {
        Cart cart = cartService.getActiveCartByUserId(userId);
        List<CartItemDTO> items = new ArrayList<>();
        for (CartItem cartItem : cartService.getItemByCartId(cart.getId())) {
            ProductDetails product = productFeigns.getProductById(cartItem.getId()).getBody().getData();
            items.add(
                new CartItemDTO(
                    cartItem.getId(), 
                    cartItem.getQuantity(),
                    product                 
                )
            );
        }

        return ResponseEntity.ok(
            new ApiResponse<CartDTO>(
                true,
                "Cart retrieved successfully",
                new CartDTO(
                    cart, 
                    items
                )
            )
        );
    }
}
