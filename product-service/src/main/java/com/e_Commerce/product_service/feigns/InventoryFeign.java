package com.e_Commerce.product_service.feigns;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.e_Commerce.product_service.dtos.Inventory;
import com.e_Commerce.product_service.res.ApiResponse;

@FeignClient(name = "inventory-service")
public interface InventoryFeign {

    @GetMapping("/api/inventories/product/{productId}")
    public ResponseEntity<ApiResponse<Inventory>> getInventoryByProductId(
        @PathVariable Long productId
    );
}