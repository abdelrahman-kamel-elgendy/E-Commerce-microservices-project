package com.e_Commerce.inventory_service.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.e_Commerce.inventory_service.dto.request.InventoryRequest;
import com.e_Commerce.inventory_service.dto.request.UpdateInventory;
import com.e_Commerce.inventory_service.dto.response.InventoryResponse;
import com.e_Commerce.inventory_service.exceptions.ResourceAlreadyExistsException;
import com.e_Commerce.inventory_service.exceptions.ResourceNotFoundException;
import com.e_Commerce.inventory_service.models.Inventory;
import com.e_Commerce.inventory_service.repositories.InventoryRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class InventoryService {
    @Autowired
    InventoryRepository inventoryRepository;

    public InventoryResponse createInventory(InventoryRequest request) {
        if (inventoryRepository.existsByCode(request.getCode()))
            throw new ResourceAlreadyExistsException("Inventory already exists!");

        return new InventoryResponse(inventoryRepository.save(new Inventory(
                request.getName(),
                request.getCode(),
                request.getAddress(),
                request.getCity(),
                request.getCountry(),
                request.getPostalCode())));
    }

    public InventoryResponse getInventoryById(Long id) {
        return new InventoryResponse(this.findInventoryById(id));
    }

    public InventoryResponse getInventoryByCode(String code) {
        return new InventoryResponse(this.findInventoryByCode(code));
    }

    public Page<InventoryResponse> getAllInventories(Pageable pageable) {
        return inventoryRepository.findAll(pageable).map(inventory -> new InventoryResponse(inventory));
    }

    public Page<InventoryResponse> getActiveInventories(Pageable pageable) {
        return inventoryRepository.findByActiveTrue(pageable).map(inventory -> new InventoryResponse(inventory));
    }

    public InventoryResponse updateInventory(Long id, UpdateInventory request) {
        Inventory inventory = this.findInventoryById(id);
        if (inventoryRepository.existsByCode(request.getCode()))
            throw new ResourceAlreadyExistsException("inventory with code is alrady exists!");

        if (request.getName() != null)
            inventory.setName(request.getName());
        if (request.getCode() != null)
            inventory.setCode(request.getCode());
        if (request.getAddress() != null)
            inventory.setAddress(request.getAddress());
        if (request.getCity() != null)
            inventory.setCity(request.getCity());
        if (request.getPostalCode() != null)
            inventory.setPostalCode(request.getPostalCode());
        if (request.getCountry() != null)
            inventory.setCountry(request.getCountry());

        return new InventoryResponse(inventoryRepository.save(inventory));
    }

    public InventoryResponse updateInventoryStatus(Long id, boolean status) {
        Inventory inventory = this.findInventoryById(id);
        inventory.setActive(status);
        return new InventoryResponse(inventoryRepository.save(inventory));
    }

    private Inventory findInventoryById(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No inventory found with id: " + id));
    }

    private Inventory findInventoryByCode(String code) {
        return inventoryRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("No inventory found with code: " + code));
    }

}
