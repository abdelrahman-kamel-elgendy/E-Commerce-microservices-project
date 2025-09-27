package com.e_Commerce.inventory_service.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.e_Commerce.inventory_service.dto.request.InventoryItemRequest;
import com.e_Commerce.inventory_service.dto.request.UpdateInventoryItemRequest;
import com.e_Commerce.inventory_service.dto.response.InventoryItemResponse;
import com.e_Commerce.inventory_service.exceptions.ResourceAlreadyExistsException;
import com.e_Commerce.inventory_service.exceptions.ResourceNotFoundException;
import com.e_Commerce.inventory_service.feigns.ProductServiceClient;
import com.e_Commerce.inventory_service.models.Inventory;
import com.e_Commerce.inventory_service.models.InventoryItem;
import com.e_Commerce.inventory_service.models.MovementType;
import com.e_Commerce.inventory_service.models.StockMovement;
import com.e_Commerce.inventory_service.repositories.InventoryItemRepository;
import com.e_Commerce.inventory_service.repositories.InventoryRepository;
import com.e_Commerce.inventory_service.repositories.StockMovementRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class InventoryItemService {
    @Autowired
    InventoryItemRepository inventoryItemRepository;

    @Autowired
    InventoryRepository inventoryRepository;

    @Autowired
    ProductServiceClient productServiceClient;

    @Autowired
    StockMovementRepository stockMovementRepository;

    public InventoryItemResponse createInventoryItem(InventoryItemRequest request) {
        // Validate inventory exists
        Inventory inventory = this.findInventoryById(request.getInventoryId());

        // Validate product exists via Product Service
        if (!productServiceClient.checkProductExistence(request.getSku()).getBody()) {
            throw new ResourceNotFoundException(
                    "No product found with SKU: " + request.getSku());
        }

        // Check if item already exists in this inventory
        if (inventoryItemRepository.existsByInventoryIdAndSku(
                request.getInventoryId(), request.getSku())) {
            throw new ResourceAlreadyExistsException("Item already exists in this inventory!");
        }

        // Create new inventory item
        InventoryItem item = new InventoryItem(
                inventory,
                request.getSku(),
                request.getQuantity());

        // Set optional fields
        if (request.getMinStockLevel() != null)
            item.setMinStockLevel(request.getMinStockLevel());
        if (request.getMaxStockLevel() != null)
            item.setMaxStockLevel(request.getMaxStockLevel());
        if (request.getReorderPoint() != null)
            item.setReorderPoint(request.getReorderPoint());
        if (request.getExpiryDate() != null)
            item.setExpiryDate(request.getExpiryDate());

        // Save the item
        InventoryItem savedItem = inventoryItemRepository.save(item);

        // Record initial stock movement
        StockMovement initialMovement = new StockMovement(
                savedItem,
                MovementType.INBOUND,
                savedItem.getQuantity(),
                "Initial stock creation");
        stockMovementRepository.save(initialMovement);

        return new InventoryItemResponse(savedItem);
    }

    public Page<InventoryItemResponse> getAllItems(Pageable pageable) {
        Page<InventoryItem> itemsPage = inventoryItemRepository.findAll(pageable);

        List<InventoryItemResponse> responses = itemsPage.getContent().stream()
                .map(item -> new InventoryItemResponse(item))
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, itemsPage.getTotalElements());
    }

    public InventoryItemResponse getItemById(Long id) {
        return new InventoryItemResponse(this.findItemById(id));
    }

    public Page<InventoryItemResponse> getItemsByInventory(Long inventoryId, Pageable pageable) {
        Page<InventoryItem> itemsPage = inventoryItemRepository.findByInventoryId(inventoryId, pageable);

        List<InventoryItemResponse> responses = itemsPage.getContent().stream()
                .map(item -> new InventoryItemResponse(item))
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, itemsPage.getTotalElements());
    }

    public InventoryItemResponse updateInventoryItemStatus(Long id, boolean status) {
        InventoryItem item = this.findItemById(id);

        if (!item.getActive() && !status)
            throw new ResourceNotFoundException("Item is already not active");

        if (item.getActive() && status)
            throw new ResourceAlreadyExistsException("Item is already active");

        // Record deletion movement
        StockMovement movement = new StockMovement(
                item,
                MovementType.ADJUSTMENT,
                status ? item.getQuantity() : 0,
                status ? "Item restored" : "Item soft deleted from inventory");
        stockMovementRepository.save(movement);

        item.setActive(status);
        return new InventoryItemResponse(inventoryItemRepository.save(item));
    }

    public InventoryItemResponse restoreItem(Long id) {
        InventoryItem item = this.findItemById(id);

        return new InventoryItemResponse(inventoryItemRepository.save(item));
    }

    public InventoryItemResponse updateInventoryItem(Long id, UpdateInventoryItemRequest request) {
        InventoryItem inventoryItem = this.findItemById(id);

        if (request.getMaxStockLevel() != null)
            inventoryItem.setMaxStockLevel(request.getMaxStockLevel());
        if (request.getMinStockLevel() != null)
            inventoryItem.setMinStockLevel(request.getMinStockLevel());
        if (request.getReorderPoint() != null)
            inventoryItem.setReorderPoint(request.getReorderPoint());
        if (request.getExpiryDate() != null)
            inventoryItem.setExpiryDate(request.getExpiryDate());

        return new InventoryItemResponse(inventoryItemRepository.save(inventoryItem));
    }

    private InventoryItem findItemById(Long id) {
        return inventoryItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No item found with id: " + id));
    }

    private Inventory findInventoryById(Long inventoryId) {
        return inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));
    }
}
