package com.e_Commerce.product_service.dtos.response;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse { 
    private Long id;
    private String name;
    private String description;
    private Long parentId;
    private String parentName;
    private Boolean active;
    private List<CategoryResponse> children;
    private Long productCount;
    private Instant createdAt;
    private Instant updatedAt;
}