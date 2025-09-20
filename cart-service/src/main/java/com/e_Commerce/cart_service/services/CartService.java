package com.e_Commerce.cart_service.services;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.e_Commerce.cart_service.dtos.CartItemResponse;
import com.e_Commerce.cart_service.dtos.CartResponse;
import com.e_Commerce.cart_service.dtos.ProductResponse;
import com.e_Commerce.cart_service.exception.InsufficientInventoryException;
import com.e_Commerce.cart_service.exception.InvalidRequestException;
import com.e_Commerce.cart_service.exception.ResourceNotFoundException;
import com.e_Commerce.cart_service.feigns.InventoryServiceClient;
import com.e_Commerce.cart_service.feigns.ProductServiceClient;
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

    @Autowired
    ProductServiceClient productServiceClient;
    
    @Autowired
    InventoryServiceClient inventoryServiceClient;


    private Cart getCartById(Long cartId) {
        return cartRepository.findById(cartId)
            .orElseThrow(() -> new ResourceNotFoundException("Cart with id: " + cartId + " not found!"));
    }

    private ProductResponse getproduct(Long productId) {
        ProductResponse product = productServiceClient.getProductById(productId).getBody();

        if(!product.getActive())
            throw new ResourceNotFoundException("Product with id: " + productId + " not found!");

        return product;
    }

    private Cart getCartByUserId(Long userId) {
        return cartRepository.findActiveCartByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Active cart with user id: " + userId + " not found!"));
    }
    
    public List<CartItemResponse> getItemByCartId(Long cartId) {
        List<CartItem> cartItems = cartItemRepository.findByCartId(cartId);
    
        return cartItems.stream()
            .map(cartItem -> new CartItemResponse(
                cartItem.getId(),
                cartItem.getQuantity(),
                this.getproduct(cartItem.getProductId())
            )
        ).collect(Collectors.toList());
    }
    
    public CartItem getItemByCartIdAndProductId(Long cartId, Long productId) {
        return cartItemRepository.findByCartIdAndProductId(cartId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item with cart id: " + cartId + " and user id: " + cartId +" not found!"));
    }

    public CartResponse getCartResponseById(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new ResourceNotFoundException("Cart with id: " + cartId + " not found!"));
        
        return new CartResponse(cart, this.getItemByCartId(cartId));
    }

    public CartResponse getCartResponseByUserId(Long userId) {
        Cart cart = cartRepository.findActiveCartByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Active cart with user id: " + userId + " not found!"));

        return new CartResponse(cart, this.getItemByCartId(cart.getId()));
    }

    public CartResponse addToCart(Long userId, String sku, Long productId, int quantity) {
        if(quantity <= 0)
            throw new InvalidRequestException("Quantity must be positive");

        if(!productServiceClient.checkProductExistence(productId, sku).getBody())
            throw new ResourceNotFoundException("Product with id: " + productId + " and sku: " + sku + " not found");

        if(!inventoryServiceClient.checkPtoductQuantity(productId, sku, quantity).getBody())
            throw new InsufficientInventoryException("Requested " + quantity + " not available");

        Cart cart; 
        try {
            cart = this.getCartByUserId(userId);
            cart.setStatus(CartStatus.ACTIVE);
            try {
                CartItem item = this.getItemByCartIdAndProductId(cart.getId(), productId);
                int newQuantity = item.getQuantity() + quantity;
                if(!inventoryServiceClient.checkPtoductQuantity(productId, sku, newQuantity).getBody()) 
                    throw new InsufficientInventoryException("Requested " + newQuantity + " not available");
                
                item.increaseQuantity(quantity);
                cartItemRepository.save(item);
            } catch (ResourceNotFoundException ex) {
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
        } catch (ResourceNotFoundException ex) {
            cart = cartRepository.save(new Cart(userId));
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

        return new CartResponse(cartRepository.save(cart), this.getItemByCartId(cart.getId()));
    }

    public CartResponse updateItemQuantity(Long cartId, Long productId, String sku, Integer quantity) {
        if(quantity < 0)
            throw new InvalidRequestException("Quantity must be positive");
        
        if(!inventoryServiceClient.checkPtoductQuantity(productId, sku, quantity).getBody())
            throw new InsufficientInventoryException("Requested " + quantity + " not available");


        Cart cart = getCartById(cartId);
        CartItem item = this.getItemByCartIdAndProductId(cartId, productId);
        if (quantity == 0) {
            this.removeItemFromCart(cartId, productId);
            cart.getItems().removeIf(i -> i.getProductId().equals(productId));
        } else {
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }
        
        return new CartResponse(cartRepository.save(cart), this.getItemByCartId(cartId));
    }

    public CartResponse removeItemFromCart(Long cartId, Long productId) {
        Cart cart = getCartById(cartId);
        CartItem item = getItemByCartIdAndProductId(cartId, productId);

        cart.removeItem(item);
        cartItemRepository.delete(item);
        
        return new CartResponse(cartRepository.save(cart), this.getItemByCartId(cartId));
    }

    public CartResponse clearCart(Long cartId) {
        Cart cart = getCartById(cartId);
        
        cartItemRepository.clearCart(cartId);
        cart.getItems().clear();
        cart.setItemCount(0);
        cart.setUpdatedAt(Instant.now());
        
        return new CartResponse(cartRepository.save(cart), this.getItemByCartId(cartId));
    }

    public CartResponse mergeCarts(Long sourceCartId, Long targetCartId) {
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

        return new CartResponse(cartRepository.save(targetCart), this.getItemByCartId(targetCartId));
    }

    public CartResponse convertToOrder(Long cartId) {
        Cart cart = getCartById(cartId);
        cart.setStatus(CartStatus.CONVERTED_TO_ORDER);
        return new CartResponse(cartRepository.save(cart), this.getItemByCartId(cartId));
    }

    public List<Cart> getUserCarts(Long userId) {
        return cartRepository.findByUserId(userId);
    }

    public void cleanupExpiredCarts() {
        Instant threshold = Instant.now().minus(Duration.ofDays(30));
        cartRepository.expireOldCarts(threshold);
    }
}
