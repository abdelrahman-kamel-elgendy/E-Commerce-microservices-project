package com.e_Commerce.inventory_service.dto.product;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductAttribute {
    private Long id;
    private String name;
    private String value;
    private Product product;
}
