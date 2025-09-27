package com.e_Commerce.inventory_service.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.e_Commerce.inventory_service.models.StockMovement;

import feign.Param;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    List<StockMovement> findByInventoryItemId(Long inventoryItemId);

    @Query("SELECT sm FROM StockMovement sm WHERE sm.inventoryItem.inventory.id = :inventoryId AND sm.inventoryItem.sku = :sku")
    Page<StockMovement> findByInventoryIdAndSku(
            @Param("inventoryId") Long inventoryId,
            @Param("sku") String sku,
            Pageable pageable);
}