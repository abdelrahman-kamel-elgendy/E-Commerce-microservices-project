package com.e_Commerce.inventory_service.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.e_Commerce.inventory_service.models.StockReservation;

import feign.Param;

public interface StockReservationRepository extends JpaRepository<StockReservation, Long> {
    List<StockReservation> findByOrderId(Long orderId);
    List<StockReservation> findByInventoryItemId(Long inventoryItemId);
    Optional<StockReservation> findByOrderIdAndInventoryItemId(Long orderId, Long inventoryItemId);
    
    @Query("SELECT SUM(sr.quantity) FROM StockReservation sr WHERE sr.inventoryItem.id = :inventoryItemId AND sr.status = 'RESERVED'")
    Integer sumReservedQuantityByInventoryItemId(@Param("inventoryItemId") Long inventoryItemId);
    
    @Query("SELECT sr FROM StockReservation sr WHERE sr.status = 'RESERVED' AND sr.expiresAt < CURRENT_TIMESTAMP")
    List<StockReservation> findExpiredReservations();
}
