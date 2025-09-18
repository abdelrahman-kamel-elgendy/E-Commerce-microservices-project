package com.e_Commerce.product_service.dtos.request;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductCreateRequest {
    @NotBlank(message = "SKU is required")
    private String sku;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;
    
    @NotNull(message = "Category is required")
    private Long categoryId;

    @NotNull(message = "Brand is required")
    private Long brandId;
    
    private List<AttributeRequest> attributes;
    private List<ImageRequest> images;
    
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AttributeRequest {
        @NotBlank(message = "Attribute name is required")
        private String name;
        
        @NotBlank(message = "Attribute value is required")
        private String value;
    }
    

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ImageRequest {
        @NotBlank(message = "Image URL is required")
        private String imageUrl;
        
        private String altText;
        private Integer displayOrder = 0;

        public ImageRequest(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }
}