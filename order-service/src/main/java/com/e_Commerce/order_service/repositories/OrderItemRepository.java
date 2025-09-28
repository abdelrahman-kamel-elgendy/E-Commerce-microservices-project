package com.e_Commerce.order_service.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.e_Commerce.order_service.models.OrderItem;

import feign.Param;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderId(Long orderId);

    List<OrderItem> findByProductId(Long productId);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id IN :orderIds")
    List<OrderItem> findByOrderIds(@Param("orderIds") List<Long> orderIds);

    @Query("SELECT oi.productId, SUM(oi.quantity) FROM OrderItem oi GROUP BY oi.productId")
    List<Object[]> findProductSales();
}