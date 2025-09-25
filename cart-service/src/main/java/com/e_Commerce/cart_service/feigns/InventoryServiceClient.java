package com.e_Commerce.cart_service.feigns;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "inventory-service")
public interface InventoryServiceClient {

    @GetMapping("/api/inventories/check")
    public ResponseEntity<Boolean> checkPtoductQuantity(
        @RequestParam Long productId,
        @RequestParam String sku,
        @RequestParam int quantity
    );
}
