package com.e_Commerce.inventory_service.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.e_Commerce.inventory_service.models.Inventory;
import com.e_Commerce.inventory_service.res.ApiResponse;
import com.e_Commerce.inventory_service.services.InventoryService;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/api/inventories")
public class InventoryController {
    @Autowired
    InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<Inventory>> createInventory(@RequestParam Long productId, @RequestParam Integer quantity) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            new ApiResponse<Inventory>(
                true, 
                "Inventory created successfully", 
                inventoryService.createInventory(productId, quantity)
            )
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Inventory>>> getAllInventory() {
        return ResponseEntity.ok(
            new ApiResponse<List<Inventory>>(
                true, 
                "All inventories retrieved successfully",
                inventoryService.getAllInventories()
            )
        );
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<Inventory>> getInventoryByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(
            new ApiResponse<Inventory>(
                true, 
                "Inventory retrieved successfully", 
                inventoryService.getInventoryByProductId(productId)
            )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Inventory>> deleteInventory(@PathVariable Long id) {
        return ResponseEntity.ok(
            new ApiResponse<Inventory>(
                true, 
                "Inventory deleted successfully", 
                inventoryService.deleteInventory(id)
            )  
        );
    }

    @GetMapping("/check")
    public ResponseEntity<ApiResponse<Boolean>> checkQuantityAvilablity(@RequestParam Long productId, @RequestParam int quantity) {
        return ResponseEntity.ok(
            new ApiResponse<Boolean>(
                true, 
                "Availability checked successfully", 
                inventoryService.checkQuantityAvailability(productId, quantity)
            )
        );
    }
    
    @PutMapping("/reduce")
    public ResponseEntity<ApiResponse<Inventory>> reduceStock(@RequestParam Long productId, @RequestParam int quantity) {
        return ResponseEntity.ok(
            new ApiResponse<Inventory>(
                true,
                "Inventory reduced successfully",
                inventoryService.reduceStock(productId, quantity)
            )
        );
    }
}
