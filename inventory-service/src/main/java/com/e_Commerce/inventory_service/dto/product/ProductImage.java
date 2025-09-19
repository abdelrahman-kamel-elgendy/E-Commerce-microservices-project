package com.e_Commerce.inventory_service.dto.product;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductImage {
    private Long id;
    private String imageUrl;
    private String altText;
    private Integer displayOrder = 0;
    private Product product;
}
