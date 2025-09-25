package com.e_Commerce.order_service.dtos.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import com.e_Commerce.order_service.models.Order;
import com.e_Commerce.order_service.models.OrderItem;
import com.e_Commerce.order_service.models.OrderStatus;
import com.e_Commerce.order_service.models.OrderStatusHistory;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderResponse {
    private Long id;
    private Long userId;
    private String orderNumber;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private String shippingAddress;
    private String customerEmail;
    private String customerPhone;
    private Instant createdAt;
    private Instant updatedAt;
    private List<OrderItemResponse> items;
    private List<OrderStatusHistory> statusHistory;

    public OrderResponse(Order order) {
        this.id = order.getId();
        this.userId = order.getUserId();
        this.orderNumber = order.getOrderNumber();
        this.status = order.getStatus();
        this.totalAmount = order.getTotalAmount();
        this.shippingAddress = order.getShippingAddress();
        this.customerEmail = order.getCustomerEmail();
        this.customerPhone = order.getCustomerPhone();
        this.createdAt = order.getCreatedAt();
        this.updatedAt = order.getUpdatedAt();
        this.items = order.getOrderItems().stream()
        .map(this::convertToOrderItemResponse)
        .collect(Collectors.toList());
        this.statusHistory = order.getStatusHistory();
    }

    private OrderItemResponse convertToOrderItemResponse(OrderItem orderItem) {
        OrderItemResponse response = new OrderItemResponse();
        response.setId(orderItem.getId());
        response.setProductId(orderItem.getProductId());
        response.setProductName(orderItem.getProductName());
        response.setQuantity(orderItem.getQuantity());
        response.setUnitPrice(orderItem.getUnitPrice());
        response.setTotalPrice(orderItem.getTotalPrice());
        return response;
    }
}
