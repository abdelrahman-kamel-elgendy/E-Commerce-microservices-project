package com.e_Commerce.inventory_service.controllers;

import com.e_Commerce.inventory_service.dto.request.InventoryRequest;
import com.e_Commerce.inventory_service.dto.request.UpdateInventory;
import com.e_Commerce.inventory_service.dto.response.InventoryResponse;
import com.e_Commerce.inventory_service.services.InventoryService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventories")
public class InventoryController {
    @Autowired
    InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<InventoryResponse> createInventory(@Valid @RequestBody InventoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryService.createInventory(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryResponse> getInventoryById(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.getInventoryById(id));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<InventoryResponse> getInventoryByCode(@PathVariable String code) {
        return ResponseEntity.ok(inventoryService.getInventoryByCode(code));
    }

    @GetMapping
    public ResponseEntity<Page<InventoryResponse>> getAllInventories(Pageable pageable) {
        return ResponseEntity.ok(inventoryService.getAllInventories(pageable));
    }

    @GetMapping("/active")
    public ResponseEntity<Page<InventoryResponse>> getActiveInventories(Pageable pageable) {
        return ResponseEntity.ok(inventoryService.getActiveInventories(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventoryResponse> updateInventory(@PathVariable Long id,
            @Valid @RequestBody UpdateInventory request) {
        return ResponseEntity.ok(inventoryService.updateInventory(id, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventoryResponse> updateInventoryStatus(@PathVariable Long id, boolean status) {
        return ResponseEntity.ok(inventoryService.updateInventoryStatus(id, status));
    }

}