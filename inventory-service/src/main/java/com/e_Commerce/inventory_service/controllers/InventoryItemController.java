package com.e_Commerce.inventory_service.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.e_Commerce.inventory_service.dto.request.InventoryItemRequest;
import com.e_Commerce.inventory_service.dto.request.UpdateInventoryItemRequest;
import com.e_Commerce.inventory_service.dto.response.InventoryItemResponse;
import com.e_Commerce.inventory_service.services.InventoryItemService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/inventory-items")
public class InventoryItemController {

    @Autowired
    private InventoryItemService inventoryItemService;

    @PostMapping
    public ResponseEntity<InventoryItemResponse> createInventoryItem(
            @Valid @RequestBody InventoryItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryItemService.createInventoryItem(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryItemResponse> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryItemService.getItemById(id));
    }

    @GetMapping("/inventory/{inventoryId}")
    public ResponseEntity<Page<InventoryItemResponse>> getItemsByInventory(
            @PathVariable Long inventoryId,
            Pageable pageable) {
        return ResponseEntity.ok(inventoryItemService.getItemsByInventory(inventoryId, pageable));
    }

    @GetMapping
    public ResponseEntity<Page<InventoryItemResponse>> getAllItems(
            Pageable pageable) {
        return ResponseEntity.ok(inventoryItemService.getAllItems(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventoryItemResponse> updateInventoryItem(@PathVariable Long id,
            @Valid @RequestBody UpdateInventoryItemRequest request) {
        return ResponseEntity.ok(inventoryItemService.updateInventoryItem(id, request));
    }

    @PostMapping("/{id}")
    public ResponseEntity<InventoryItemResponse> updateInventoryItemStatus(@PathVariable Long id, boolean status) {
        return ResponseEntity.ok(inventoryItemService.updateInventoryItemStatus(id, status));
    }

}
