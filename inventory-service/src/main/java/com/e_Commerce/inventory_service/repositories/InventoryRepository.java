package com.e_Commerce.inventory_service.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.e_Commerce.inventory_service.models.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProductIdAndSkuCode(Long productId, String sku);
    boolean existsByProductIdAndSkuCode(Long productId, String sku);
}
