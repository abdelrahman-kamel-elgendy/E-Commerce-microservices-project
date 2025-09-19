package com.e_Commerce.inventory_service.dto.product;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Product {
    private Long id;
    private String name;
    private String sku;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Category category;
    private Brand brand;
    private List<ProductAttribute> attributes = new ArrayList<>();
    private List<ProductImage> images = new ArrayList<>();
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
}