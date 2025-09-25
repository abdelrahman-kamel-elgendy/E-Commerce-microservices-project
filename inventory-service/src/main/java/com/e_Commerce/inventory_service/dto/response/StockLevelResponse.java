package com.e_Commerce.inventory_service.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockLevelResponse {
    private Long productId;
    private String sku;
    private Integer totalQuantity;
    private Integer totalReservedQuantity;
    private Integer totalAvailableQuantity;
    private Boolean lowStock;
    private Integer minAvailableQuantity;
    private List<LocationStock> locationStocks;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationStock {
        private Long inventoryId;
        private String inventoryName;
        private String inventoryCode;
        private Integer quantity;
        private Integer reservedQuantity;
        private Integer availableQuantity;
        private String location;
    }
}