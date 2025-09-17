package com.e_Commerce.inventory_service.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ResponseEntity<ApiResponse<Inventory>> createInventory(@RequestParam Long productId, @RequestParam int quantity) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            new ApiResponse<Inventory>(
                true, 
                "Inventory created successfully", 
                inventoryService.createInventory(productId, quantity)
            )
        );
    }

    @PutMapping
    public ResponseEntity<ApiResponse<Inventory>> updateStok(@RequestParam Long productId, @RequestParam int quantity) {
        return ResponseEntity.ok(
            new ApiResponse<Inventory>(
                true,
                "Inventory increased successfully",
                inventoryService.updateStock(
                    productId, 
                    quantity
                )
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

    @DeleteMapping
    public ResponseEntity<ApiResponse<Inventory>> deleteInventory(@RequestParam Long productId) {
        return ResponseEntity.ok(
            new ApiResponse<Inventory>(
                true, 
                "Inventory deleted successfully", 
                inventoryService.deleteInventory(productId)
            )  
        );
    }

    @GetMapping("/productId")
    public ResponseEntity<ApiResponse<Inventory>> getInventoryByProductId(@RequestParam Long productId) {
        return ResponseEntity.ok(
            new ApiResponse<Inventory>(
                true, 
                "Inventory retrieved successfully",
                inventoryService.getInventoryByProductId(productId)
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
        
    @PutMapping("/decrease")
    public ResponseEntity<ApiResponse<Inventory>> decreaseStock(@RequestParam Long productId, @RequestParam int quantity) {
        return ResponseEntity.ok(
            new ApiResponse<Inventory>(
                true,
                "Inventory reduced successfully",
                inventoryService.decrease(
                    productId, 
                    quantity
                )
            )
        );
    }
}
