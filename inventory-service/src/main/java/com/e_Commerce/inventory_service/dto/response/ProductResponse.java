package com.e_Commerce.inventory_service.dto.response;

import java.math.BigDecimal;
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
public class ProductResponse {
    private Long id;
    private String sku;
    private String name;
    private String description;
    private BigDecimal price;
    private String categoryName;
    private String brandName;
    private Boolean active;
    private Instant createdAt;
    private Instant updatedAt;
    private List<AttributeResponse> attributes;
    private List<ImageResponse> images;

    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AttributeResponse {
        private String name;
        private String value;
    }
    
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageResponse {
        private String imageUrl;
        private String altText;
        private Integer displayOrder;
    }
}