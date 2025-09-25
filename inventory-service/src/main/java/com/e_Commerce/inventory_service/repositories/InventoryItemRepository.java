package com.e_Commerce.inventory_service.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.e_Commerce.inventory_service.models.InventoryItem;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    boolean existsByInventoryIdAndProductIdAndSku(Long inventoryId, Long productId, String sku);

    List<InventoryItem> findByProductIdAndSkuAndActiveTrue(Long productId, String sku);

    Page<InventoryItem> findByInventoryId(Long inventoryId, Pageable pageable);

    Optional<InventoryItem> findByInventoryIdAndProductIdAndSku(Long inventoryId, Long productId, String sku);

    Optional<InventoryItem> findByInventoryIdAndProductId(Long id, Long valueOf);

    @Query("SELECT i FROM InventoryItem i WHERE i.active = true AND (i.quantity - i.reservedQuantity) <= i.minStockLevel")
    Page<InventoryItem> findLowStockItems(Pageable pageable);

}
