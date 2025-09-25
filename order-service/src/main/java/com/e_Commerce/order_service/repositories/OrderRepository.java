package com.e_Commerce.order_service.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.e_Commerce.order_service.models.Order;
import com.e_Commerce.order_service.models.OrderStatus;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);

    List<Order> findByStatus(OrderStatus status);
    
}
