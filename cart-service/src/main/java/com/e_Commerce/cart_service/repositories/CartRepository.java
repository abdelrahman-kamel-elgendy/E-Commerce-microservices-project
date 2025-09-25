package com.e_Commerce.cart_service.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.e_Commerce.cart_service.models.Cart;

import feign.Param;


public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByUserId(Long userId);

    @Query("SELECT c FROM Cart c WHERE c.userId = :userId")
    Optional<Cart> findCartByUserId(@Param("userId") Long userId);
}
