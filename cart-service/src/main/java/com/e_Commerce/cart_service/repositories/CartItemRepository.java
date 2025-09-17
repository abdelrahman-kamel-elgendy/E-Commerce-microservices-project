package com.e_Commerce.cart_service.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.e_Commerce.cart_service.models.CartItem;

import feign.Param;

public interface CartItemRepository extends JpaRepository<CartItem, Long> { 
    List<CartItem> findByCartId(Long cartId);
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.id = :cartId")
    void clearCart(@Param("cartId") Long cartId);
    
    boolean existsByCartIdAndProductId(Long cartId, Long productId);
}
