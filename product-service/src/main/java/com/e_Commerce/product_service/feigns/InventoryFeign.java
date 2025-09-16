package com.e_Commerce.product_service.feigns;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.e_Commerce.product_service.dtos.Inventory;
import com.e_Commerce.product_service.res.ApiResponse;

@FeignClient(name = "inventory-service")
public interface InventoryFeign {

    @PostMapping("/api/inventories")
    public ResponseEntity<ApiResponse<Inventory>> createInventory(
        @RequestParam Long productId, 
        @RequestParam Integer quantity
    );

    @PutMapping("/api/inventories")
    public ResponseEntity<ApiResponse<Inventory>> updateStok(
        @RequestParam Long productId, 
        @RequestParam int quantity
    );

    @GetMapping("/api/inventories/productId")
    public ResponseEntity<ApiResponse<Inventory>> getInventoryByProductId(
        @RequestParam Long productId
    );

    @DeleteMapping("/api/inventories")
    public ResponseEntity<ApiResponse<Inventory>> deleteInventory(
        @RequestParam Long productId
    );

    @PutMapping("/api/inventories/increase")
    public ResponseEntity<ApiResponse<Inventory>> increaseStock(
        @RequestParam Long productId,
        @RequestParam int quantity
    );
    
    @PutMapping("/api/inventories/decrease")
    public ResponseEntity<ApiResponse<Inventory>> decreaseStock(
        @RequestParam Long productId, 
        @RequestParam int quantity
    );

}