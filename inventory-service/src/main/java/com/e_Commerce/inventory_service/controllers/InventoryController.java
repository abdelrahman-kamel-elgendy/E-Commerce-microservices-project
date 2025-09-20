package com.e_Commerce.inventory_service.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.e_Commerce.inventory_service.dto.request.InventoryRequest;
import com.e_Commerce.inventory_service.dto.response.InventoryResponse;
import com.e_Commerce.inventory_service.models.Inventory;
import com.e_Commerce.inventory_service.models.MovementType;
import com.e_Commerce.inventory_service.services.InventoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/inventories")
public class InventoryController {
    @Autowired
    InventoryService service;

    @PostMapping
    public ResponseEntity<InventoryResponse> createInventory(@Valid @RequestBody InventoryRequest inventory) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createInventory(inventory));
    }

    @GetMapping
    public ResponseEntity<Page<InventoryResponse>> getAllInventories(Pageable pageable) {
        return ResponseEntity.ok(service.getAllInventories(pageable));
    }

    @GetMapping("/product")
    public ResponseEntity<Inventory> getPtoductInventory(
        @RequestParam Long productId,
        @RequestParam String sku
    ) {
        return ResponseEntity.ok(service.getPtoductInventory(productId, sku));
    }

    @PutMapping("/increase")
    public ResponseEntity<Inventory> increaseInventory(
        @RequestParam Long productId,
        @RequestParam String sku,
        @RequestParam int quantity
    ) {
        return ResponseEntity.ok(service.increaseInventory(productId, sku, quantity));
    }

    @PutMapping("/decrease")
    public ResponseEntity<Inventory> decreaseInventory(
        @RequestParam Long productId,
        @RequestParam String sku,
        @RequestParam int quantity
    ) {
        return ResponseEntity.ok(service.decreaseInventory(productId, sku, quantity));
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> checkPtoductQuantity(
        @RequestParam Long productId,
        @RequestParam String sku,
        @RequestParam int quantity
    ) {
        return ResponseEntity.ok(service.checkPtoductQuantity(productId, sku, quantity));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<Page<InventoryResponse>> getLowStockItems() {
        return ResponseEntity.ok(service.getLowStockItems());
    }


    @PostMapping("/adjust")
    public ResponseEntity<Inventory> adjustStock(
        @RequestParam Long id,
        @RequestParam Integer quantity,
        @RequestParam String reason,
        @RequestParam MovementType movementType
    ) {
        return ResponseEntity.ok(service.adjustStock(id, quantity, reason, movementType));
    }

    @PostMapping("/reserve")
    public ResponseEntity<Inventory> reserveStock(
        @RequestParam Long inventoryId,
        @RequestParam Integer quantity,
        @RequestParam Long orderId
    ) {
        return ResponseEntity.ok(service.reserveStock(inventoryId, quantity, orderId));
    }

    @PostMapping("/release")
    public ResponseEntity<Inventory> releaseReservedStock(
        @RequestParam Long inventoryId,
        @RequestParam Integer quantity,
        @RequestParam Long orderId
    ) {
        return ResponseEntity.ok(service.releaseReservedStock(inventoryId, quantity, orderId));
    }
}
