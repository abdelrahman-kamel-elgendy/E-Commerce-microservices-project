package com.e_Commerce.inventory_service.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.e_Commerce.inventory_service.models.Inventory;
import com.e_Commerce.inventory_service.repositories.InventoryRepository;

@Service
public class InventoryService {
    @Autowired
    InventoryRepository inventoryRepository;

    public Inventory createInventory(Long productId, Integer quantity) {
        return inventoryRepository.save(new Inventory(productId, quantity));
    }

    public List<Inventory> getAllInventories() {
        return inventoryRepository.findAll();
    }

    public Inventory getInventoryByProductId(Long productId) {
        return inventoryRepository.findByProductId(productId)
            .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inventory with product id: "+ productId + " not found"));
    }

    public Inventory getInventoryById(Long id) {
        return inventoryRepository.findById(id)
            .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inventory with id: "+ id + " not found"));
    }

    public Inventory deleteInventory(Long id) {
        Inventory inventory = this.getInventoryById(id);
        inventoryRepository.delete(inventory);
        return inventory;
    }

    public Boolean checkQuantityAvailability(Long productId, int quantity) {
        return this.getInventoryByProductId(productId).getQuantity() >= quantity; 
    }

    public Inventory reduceStock(Long productId, int quantity) {
        Inventory inventory = this.getInventoryByProductId(productId);
        int inventoryQuantity = inventory.getQuantity();

        if(inventoryQuantity < quantity)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough stock to fulfill the order");

        inventory.setQuantity(inventoryQuantity - quantity);
        return inventoryRepository.save(inventory);
    }
}
