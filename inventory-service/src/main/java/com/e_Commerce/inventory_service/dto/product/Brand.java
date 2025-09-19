package com.e_Commerce.inventory_service.dto.product;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Brand {
    private Long id;
    private String name;
    private String description;
    private String logoUrl;
    private List<Product> products;
    private Boolean active;
}