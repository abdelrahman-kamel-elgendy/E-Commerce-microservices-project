package com.e_Commerce.order_service.dtos.response;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CartItemResponse {
    private Long id;
    private Long productId;
    private String productSku;
    private String productName;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal totalPrice;
    private String productImageUrl;
    private String productImageAltText;
}
