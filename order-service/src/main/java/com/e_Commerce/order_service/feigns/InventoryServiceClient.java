package com.e_Commerce.order_service.feigns;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.e_Commerce.order_service.dtos.response.Inventory;

@FeignClient(name = "inventory-service")
public interface InventoryServiceClient {

    @PostMapping("/api/inventories/reserve")
    public ResponseEntity<Inventory> reserveStock(
        @RequestParam Long productId,
        @RequestParam String sku,
        @RequestParam Integer quantity,
        @RequestParam Long orderId
    );

    @PostMapping("/api/inventories/release")
    public ResponseEntity<Inventory> releaseReservedStock(
        @RequestParam Long productId,
        @RequestParam String sku,
        @RequestParam Integer quantity,
        @RequestParam Long orderId
    );

    @GetMapping("/api/inventories/check")
    public ResponseEntity<Boolean> checkPtoductQuantity(
        @RequestParam Long productId,
        @RequestParam String sku,
        @RequestParam int quantity
    );


}
