package com.e_Commerce.inventory_service.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.e_Commerce.inventory_service.dto.request.StockAdjustmentRequest;
import com.e_Commerce.inventory_service.dto.response.InventoryItemResponse;
import com.e_Commerce.inventory_service.dto.response.StockLevelResponse;
import com.e_Commerce.inventory_service.dto.response.StockReservationResponse;
import com.e_Commerce.inventory_service.services.InventoryManagementService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/inventory-management")
public class InventoryManagementController {
    @Autowired
    InventoryManagementService inventoryManagementService;

    @GetMapping("/stock-level")
    public ResponseEntity<StockLevelResponse> getStockLevel(
            @RequestParam String sku) {
        return ResponseEntity.ok(inventoryManagementService.getStockLevel(sku));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<Page<InventoryItemResponse>> getLowStockItems(
            Pageable pageable) {
        return ResponseEntity.ok(inventoryManagementService.getLowStockItems(pageable));
    }

    @PostMapping("/adjust-stock")
    public ResponseEntity<InventoryItemResponse> adjustStock(
            @Valid @RequestBody StockAdjustmentRequest request) {
        return ResponseEntity.ok(inventoryManagementService.adjustStock(request));
    }

    @PostMapping("/{fromItemId}/transfer")
    public ResponseEntity<InventoryItemResponse> transferStock(
            @PathVariable Long fromItemId,
            @RequestParam Long toInventoryId,
            @RequestParam Integer quantity,
            @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(inventoryManagementService.transferStock(
                fromItemId, toInventoryId, quantity, reason));
    }

    @GetMapping("/check-availability")
    public ResponseEntity<Map<String, Object>> checkAvailability(
            @RequestParam String sku,
            @RequestParam Integer quantity) {

        try {
            StockLevelResponse stockLevel = inventoryManagementService.getStockLevel(sku);
            boolean isAvailable = stockLevel.getTotalAvailableQuantity() >= quantity;

            Map<String, Object> response = new HashMap<>();
            response.put("sku", sku);
            response.put("requestedQuantity", quantity);
            response.put("availableQuantity", stockLevel.getTotalAvailableQuantity());
            response.put("isAvailable", isAvailable);
            response.put("lowStock", stockLevel.getLowStock());

            if (!isAvailable)
                response.put("message", "Insufficient stock available");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sku", sku);
            response.put("requestedQuantity", quantity);
            response.put("isAvailable", false);
            response.put("message", "Product/SKU not found in inventory");
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/reserve")
    public ResponseEntity<List<StockReservationResponse>> reserveStock(
            @RequestParam String sku,
            @RequestParam int quantity,
            @RequestParam Long orderId) {
        return ResponseEntity.ok(inventoryManagementService.reserveStock(sku, quantity, orderId));
    }
}
