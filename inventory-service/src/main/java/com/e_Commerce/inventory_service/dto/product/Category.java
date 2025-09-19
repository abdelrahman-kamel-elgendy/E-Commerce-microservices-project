package com.e_Commerce.inventory_service.dto.product;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Category {
    private Long id;
    private String name;
    private String description;
    private Category parent;
    private List<Category> children = new ArrayList<>();
    private List<Product> products = new ArrayList<>();
    private Boolean active;
    private Instant createdAt;
    private Instant updatedAt;
}