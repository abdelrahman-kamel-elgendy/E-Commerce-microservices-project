package com.e_Commerce.inventory_service.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.e_Commerce.inventory_service.models.StockMovement;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    List<StockMovement> findByInventoryItemId(Long inventoryItemId);

    Page<StockMovement> findByInventoryItemIdAndProductId(Long inventoryId, Long productId, Pageable pageable);
}