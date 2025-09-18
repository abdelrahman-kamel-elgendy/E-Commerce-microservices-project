package com.e_Commerce.product_service.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BrandRequest {
    @NotBlank(message = "Name is required")
    private String name;
    
    private String description;
    private String logoUrl;
}
