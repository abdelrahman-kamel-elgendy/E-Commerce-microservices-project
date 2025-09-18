package com.e_Commerce.product_service.dtos.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryTreeResponse {
    
    private Long id;
    private String name;
    private List<CategoryTreeResponse> children;
}