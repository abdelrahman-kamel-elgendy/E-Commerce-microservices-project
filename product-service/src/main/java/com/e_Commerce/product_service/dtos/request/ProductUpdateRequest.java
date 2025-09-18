package com.e_Commerce.product_service.dtos.request;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductUpdateRequest {
    private String name;
    private String sku;
    private String description;
    
    @Positive(message = "Price must be positive")
    private BigDecimal price;
    
    private Long categoryId;
    private Long brandId;
    private Boolean active;

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
