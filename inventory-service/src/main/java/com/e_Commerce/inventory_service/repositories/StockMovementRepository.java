package com.e_Commerce.inventory_service.repositories;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.e_Commerce.inventory_service.models.StockMovement;
import com.e_Commerce.inventory_service.models.MovementType;

import feign.Param;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    
    List<StockMovement> findByInventoryId(Long inventoryId);
    
    List<StockMovement> findByMovementType(MovementType movementType);
    
    List<StockMovement> findByReferenceId(String referenceId);
    
    List<StockMovement> findByCreatedAtBetween(Instant startDate, Instant endDate);
    
    @Query("SELECT sm FROM StockMovement sm WHERE sm.inventoryId = :inventoryId AND sm.createdAt BETWEEN :startDate AND :endDate")
    List<StockMovement> findByInventoryIdAndDateRange(
        @Param("inventoryId") Long inventoryId,
        @Param("startDate") Instant startDate,
        @Param("endDate") Instant endDate
    );
}