package com.e_Commerce.product_service.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDto {
    @NotBlank(message = "name is required!")
    private String name;

    private String description;
    private String imageUrl;
}
