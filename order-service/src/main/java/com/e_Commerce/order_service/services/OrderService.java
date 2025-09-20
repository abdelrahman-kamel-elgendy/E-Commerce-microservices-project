package com.e_Commerce.order_service.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.e_Commerce.order_service.dtos.request.OrderRequest;
import com.e_Commerce.order_service.dtos.response.OrderResponse;
import com.e_Commerce.order_service.models.OrderStatus;

@Service
public class OrderService {

    public OrderResponse createOrder(OrderRequest orderRequest) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createOrder'");
    }

    public OrderResponse getOrderById(Long orderId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getOrderById'");
    }

    public List<OrderResponse> getOrdersByUserId(Long userId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getOrdersByUserId'");
    }

    public List<OrderResponse> getOrdersByStatus(OrderStatus status) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getOrdersByStatus'");
    }

    public OrderResponse updateOrderStatus(Long orderId, OrderStatus status, String notes) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateOrderStatus'");
    }

    public void cancelOrder(Long orderId, String reason) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'cancelOrder'");
    }
    
}
