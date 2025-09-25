package com.e_Commerce.order_service.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.e_Commerce.order_service.models.OrderStatusHistory;

public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, Long> {
    List<OrderStatusHistory> findByOrderId(Long orderId);
    List<OrderStatusHistory> findByOrderIdOrderByChangedAtDesc(Long orderId);
}