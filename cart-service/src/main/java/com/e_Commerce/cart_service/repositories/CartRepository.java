package com.e_Commerce.cart_service.repositories;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.e_Commerce.cart_service.models.Cart;
import com.e_Commerce.cart_service.models.CartStatus;

import feign.Param;


public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserIdAndStatus(Long userId, CartStatus status);
    List<Cart> findByUserId(Long userId);
    List<Cart> findByStatus(CartStatus status);

    @Query("SELECT c FROM Cart c WHERE c.userId = :userId AND c.status = 'ACTIVE'")
    Optional<Cart> findActiveCartByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE Cart c SET c.status = 'EXPIRED' WHERE c.updatedAt < :threshold AND c.status = 'ACTIVE'")
    int expireOldCarts(@Param("threshold") Instant threshold);
    
    boolean existsByUserIdAndStatus(Long userId, CartStatus status);
}
