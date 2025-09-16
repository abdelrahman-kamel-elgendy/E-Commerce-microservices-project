package com.e_Commerce.product_service.dtos;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDto {
    
    @NotBlank(message = "Name is required!")
    private String name;

    private String description;

    @NotNull(message = "Price is required!")
    @Positive(message = "Price must be a positive number!")
    private BigDecimal price;

    @NotNull(message = "Quantity is required!")
    @Positive(message = "Quantity must be a positive number!")
    private int quantity; 

    @NotNull(message = "Category ID is required!")
    @Positive(message = "Category ID must be a positive number!")
    private Long categoryId;

    private String imageUrl;
}