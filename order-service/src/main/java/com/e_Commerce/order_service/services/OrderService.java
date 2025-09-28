package com.e_Commerce.order_service.services;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.e_Commerce.order_service.dtos.request.OrderRequest;
import com.e_Commerce.order_service.dtos.response.OrderResponse;
import com.e_Commerce.order_service.exception.ResourceNotFoundException;
import com.e_Commerce.order_service.feigns.CartServiceClient;
import com.e_Commerce.order_service.feigns.InventoryServiceClient;
import com.e_Commerce.order_service.models.Order;
import com.e_Commerce.order_service.models.OrderItem;
import com.e_Commerce.order_service.models.OrderStatus;
import com.e_Commerce.order_service.repositories.OrderRepository;

@Service
public class OrderService {
    @Autowired
    CartServiceClient cartServiceClient;
    @Autowired
    InventoryServiceClient inventoryServiceClient;
    @Autowired
    OrderRepository orderRepository;

    public OrderResponse createOrder(OrderRequest request) {
        String 
        
        Order order = new Order(request.getOrder, request.getUserId())
        
        List<OrderItem> orderItems = cart.getItems().stream()
                .map(item -> new OrderItem(order, item))
                .collect(Collectors.toList());
        order.setOrderItems(orderItems);

        Order savedOrder = orderRepository.save(order);
        addStatusHistory(savedOrder, OrderStatus.PENDING, "Order created");

        for (CartItemResponse item : cart.getItems()) 
            inventoryServiceClient.reserveStock(item.getProductId(), item.getProductSku(), item.getQuantity(), savedOrder.getId());

        cartServiceClient.clearCart(cart.getId());

        return new OrderResponse(savedOrder);
    }

    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        return new OrderResponse(order);

    }

    public List<OrderResponse> getOrdersByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(order -> new OrderResponse(order))
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getOrdersByStatus(OrderStatus status) {
        List<Order> orders = orderRepository.findByStatus(status);
        return orders.stream()
                .map(order -> new OrderResponse(order))
                .collect(Collectors.toList());
    }

    public OrderResponse updateOrderStatus(Long orderId, OrderStatus status, String notes) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        order.setStatus(status);
        order.setUpdatedAt(Instant.now());

        Order updatedOrder = orderRepository.save(order);
        addStatusHistory(updatedOrder, status, notes);

        return new OrderResponse(updatedOrder);
    }

    public void cancelOrder(Long orderId, String reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(Instant.now());

        orderRepository.save(order);
        addStatusHistory(order, OrderStatus.CANCELLED, "Order cancelled: " + reason);

        for (OrderItem item : order.getOrderItems())
            inventoryServiceClient.releaseReservedStock(item.getProductId(), item.getSku(), item.getQuantity(),
                    order.getId());
    }

    private void addStatusHistory(Order order, OrderStatus status, String notes) {
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setStatus(status);
        history.setChangedAt(Instant.now());
        history.setNotes(notes);
        orderStatusHistoryRepository.save(history);
    }
}
