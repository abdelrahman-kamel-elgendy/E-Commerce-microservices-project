package com.e_Commerce.product_service.dtos;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProductDetails {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String categoryName;
    private int stockQuantity;
    private String imageUrl;
}
