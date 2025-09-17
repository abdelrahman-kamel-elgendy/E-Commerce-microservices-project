package com.e_Commerce.cart_service.services;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.e_Commerce.cart_service.models.Cart;
import com.e_Commerce.cart_service.models.CartItem;
import com.e_Commerce.cart_service.models.CartStatus;
import com.e_Commerce.cart_service.repositories.CartItemRepository;
import com.e_Commerce.cart_service.repositories.CartRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CartService {

    @Autowired
    CartRepository cartRepository;
    
    @Autowired
    CartItemRepository cartItemRepository;

    public List<CartItem> getItemByCartId(Long cartId) {
        return cartItemRepository.findByCartId(cartId);
    }
    
    public CartItem getItemByCartIdAndProductId(Long cartId, Long productId) {
        return cartItemRepository.findByCartIdAndProductId(cartId, productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart item with cart id: " + cartId + " and user id: " + cartId +" not found!"));
    }

    public Cart getOrCreateCart(Long userId) {
        try {
            return this.getActiveCartByUserId(userId);

        } catch (ResponseStatusException ex) {
            
            return cartRepository.save(new Cart(userId));
        }
    }

    public Cart getCartById(Long cartId) {
        return cartRepository.findById(cartId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart with id: " + cartId + " not found!"));
    }

    public Cart getActiveCartByUserId(Long userId) {
        return cartRepository.findActiveCartByUserId(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Active cart with user id: " + userId + " not found!"));
    }

    public Cart addItemToCart(Long cartId, Long productId, int quantity) {
        Cart cart = getCartById(cartId);
        
        try{
            CartItem item = this.getItemByCartIdAndProductId(cartId, productId);
            item.increaseQuantity(quantity);
            cartItemRepository.save(item);

        } catch(ResponseStatusException ex) {
            cart.getItems().add(
                cartItemRepository.save(
                    new CartItem(
                        cart, 
                        productId, 
                        quantity
                    )
                )
            );
        }

        return cartRepository.save(cart);
    }

    public Cart updateItemQuantity(Long cartId, Long productId, Integer quantity) {
        Cart cart = getCartById(cartId);
        
        CartItem item = this.getItemByCartIdAndProductId(cartId, productId);
        
        if (quantity <= 0) {
            this.removeItemFromCart(cartId, productId);
            cart.getItems().removeIf(i -> i.getProductId().equals(productId));
        } else {
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }
        
        return cartRepository.save(cart);
    }

    public Cart removeItemFromCart(Long cartId, Long productId) {
        Cart cart = getCartById(cartId);
        CartItem item = getItemByCartIdAndProductId(cartId, productId);

        cart.removeItem(item);
        cartItemRepository.delete(item);
        
        return cartRepository.save(cart);
    }

    public Cart clearCart(Long cartId) {
        Cart cart = getCartById(cartId);
        
        cartItemRepository.clearCart(cartId);
        cart.getItems().clear();
        cart.setItemCount(0);
        cart.setUpdatedAt(Instant.now());
        
        return cartRepository.save(cart);
    }

    public Cart mergeCarts(Long sourceCartId, Long targetCartId) {
        Cart sourceCart = getCartById(sourceCartId);
        Cart targetCart = getCartById(targetCartId);
        
        for (CartItem sourceItem : sourceCart.getItems()) {

            try {
                CartItem item = this.getItemByCartIdAndProductId(targetCartId, sourceItem.getProductId());
                
                item.increaseQuantity(sourceItem.getQuantity());
                cartItemRepository.save(item);
            } catch(ResponseStatusException ex) {                
                targetCart.addItem(
                    cartItemRepository.save(
                        new CartItem(
                            targetCart,
                            sourceItem.getProductId(),
                            sourceItem.getQuantity()
                        )
                    )
                );
            }
        }
        
        sourceCart.setStatus(CartStatus.MERGED);
        cartRepository.save(sourceCart);

        return cartRepository.save(targetCart);
    }

    public Cart abandonCart(Long cartId) {
        Cart cart = getCartById(cartId);
        cart.setStatus(CartStatus.ABANDONED);
        return cartRepository.save(cart);
    }

    public Cart convertToOrder(Long cartId) {
        Cart cart = getCartById(cartId);
        cart.setStatus(CartStatus.CONVERTED_TO_ORDER);
        return cartRepository.save(cart);
    }

    public List<Cart> getUserCarts(Long userId) {
        return cartRepository.findByUserId(userId);
    }

    public void cleanupExpiredCarts() {
        Instant threshold = Instant.now().minus(Duration.ofDays(30));
        cartRepository.expireOldCarts(threshold);
    }
}
