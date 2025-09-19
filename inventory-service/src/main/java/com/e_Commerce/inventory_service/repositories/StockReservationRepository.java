package com.e_Commerce.inventory_service.repositories;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.e_Commerce.inventory_service.models.ReservationStatus;
import com.e_Commerce.inventory_service.models.StockReservation;

import feign.Param;

public interface StockReservationRepository extends JpaRepository<StockReservation, Long> {
    List<StockReservation> findByOrderId(Long orderId);
    List<StockReservation> findByInventoryId(Long inventoryId);
    List<StockReservation> findByStatus(ReservationStatus status);
    Optional<StockReservation> findByOrderIdAndInventoryId(Long orderId, Long inventoryId);
    
    @Query("SELECT sr FROM StockReservation sr WHERE sr.expiresAt < :currentTime AND sr.status = 'RESERVED'")
    List<StockReservation> findExpiredReservations(@Param("currentTime") Instant currentTime);
    
    @Modifying
    @Query("UPDATE StockReservation sr SET sr.status = 'EXPIRED' WHERE sr.expiresAt < :currentTime AND sr.status = 'RESERVED'")
    int expireOldReservations(@Param("currentTime") Instant currentTime);
    
    @Query("SELECT SUM(sr.quantity) FROM StockReservation sr WHERE sr.inventoryId = :inventoryId AND sr.status = 'RESERVED'")
    Integer sumReservedQuantityByInventoryId(@Param("inventoryId") Long inventoryId);
}
