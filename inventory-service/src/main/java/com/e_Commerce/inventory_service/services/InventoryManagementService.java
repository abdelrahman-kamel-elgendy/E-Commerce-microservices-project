package com.e_Commerce.inventory_service.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.e_Commerce.inventory_service.dto.request.StockAdjustmentRequest;
import com.e_Commerce.inventory_service.dto.response.InventoryItemResponse;
import com.e_Commerce.inventory_service.dto.response.StockLevelResponse;
import com.e_Commerce.inventory_service.exceptions.InsufficientStockException;
import com.e_Commerce.inventory_service.exceptions.ResourceNotFoundException;
import com.e_Commerce.inventory_service.models.Inventory;
import com.e_Commerce.inventory_service.models.InventoryItem;
import com.e_Commerce.inventory_service.models.MovementType;
import com.e_Commerce.inventory_service.models.StockMovement;
import com.e_Commerce.inventory_service.models.StockReservation;
import com.e_Commerce.inventory_service.repositories.InventoryItemRepository;
import com.e_Commerce.inventory_service.repositories.InventoryRepository;
import com.e_Commerce.inventory_service.repositories.StockMovementRepository;
import com.e_Commerce.inventory_service.repositories.StockReservationRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class InventoryManagementService {
    @Autowired
    InventoryRepository inventoryRepository;

    @Autowired
    InventoryItemRepository inventoryItemRepository;

    @Autowired
    StockReservationRepository stockReservationRepository;

    @Autowired
    StockMovementRepository stockMovementRepository;

    public InventoryItemResponse adjustStock(StockAdjustmentRequest request) {
        InventoryItem item = this.findItemById(request.getInventoryItemId());

        MovementType movementType = request.getMovementType();
        int oldQuantity = item.getQuantity();
        int newQuantity = oldQuantity;

        // Perform stock adjustment based on movement type
        switch (movementType) {
            case INBOUND:
                newQuantity = oldQuantity + request.getQuantity();
                break;

            case OUTBOUND:
                int availableQuantity = item.getAvailableQuantity();
                if (availableQuantity < request.getQuantity())
                    throw new InsufficientStockException(
                            String.format("Insufficient stock. Available: %d, Requested: %d",
                                    availableQuantity, request.getQuantity()));

                newQuantity = oldQuantity - request.getQuantity();
                break;

            case ADJUSTMENT:
                newQuantity = request.getQuantity();
                break;

            case RESERVATION:
                if (item.getAvailableQuantity() < request.getQuantity())
                    throw new InsufficientStockException("Insufficient available quantity for reservation");
                item.setReservedQuantity(item.getReservedQuantity() + request.getQuantity());
                break;

            case RELEASE:
                if (item.getReservedQuantity() < request.getQuantity())
                    throw new IllegalArgumentException("Cannot release more than reserved quantity");

                item.setReservedQuantity(item.getReservedQuantity() - request.getQuantity());
                break;

            default:
                throw new IllegalArgumentException("Unsupported movement type: " + movementType);
        }

        // Update quantity for non-reservation movements
        if (movementType != MovementType.RESERVATION && movementType != MovementType.RELEASE)
            item.setQuantity(newQuantity);

        InventoryItem updatedItem = inventoryItemRepository.save(item);

        // Record stock movement
        StockMovement movement = new StockMovement(
                item,
                movementType,
                request.getQuantity(),
                request.getReason() != null ? request.getReason() : "Stock adjustment");
        movement.setReferenceId(request.getReferenceId());
        movement.setCreatedBy(request.getCreatedBy());
        stockMovementRepository.save(movement);

        return new InventoryItemResponse(updatedItem);
    }

    public StockLevelResponse getStockLevel(Long productId, String sku) {
        // Find all inventory items for this product/SKU
        List<InventoryItem> items = inventoryItemRepository.findByProductIdAndSkuAndActiveTrue(productId, sku);

        if (items.isEmpty())
            throw new ResourceNotFoundException(
                    "No inventory items found for product: " + productId + " and SKU: " + sku);

        // Calculate total quantities across all inventories
        int totalQuantity = items.stream().mapToInt(InventoryItem::getQuantity).sum();
        int totalReserved = items.stream().mapToInt(InventoryItem::getReservedQuantity).sum();
        int totalAvailable = totalQuantity - totalReserved;

        // Find low stock items
        List<InventoryItem> lowStockItems = items.stream()
                .filter(item -> item.getAvailableQuantity() <= item.getMinStockLevel())
                .collect(Collectors.toList());

        // Get inventory details for locations
        List<StockLevelResponse.LocationStock> locationStocks = items.stream()
                .map(item -> {
                    return new StockLevelResponse.LocationStock(
                            item.getInventory().getId(),
                            item.getInventory().getName(),
                            item.getInventory().getCode(),
                            item.getQuantity(),
                            item.getReservedQuantity(),
                            item.getAvailableQuantity(),
                            item.getInventory().getAddress() + ", " + item.getInventory().getCity() + ", " + item
                                    .getInventory().getCountry());
                })
                .collect(Collectors.toList());

        return new StockLevelResponse(
                productId,
                sku,
                totalQuantity,
                totalReserved,
                totalAvailable,
                lowStockItems.size() > 0,
                lowStockItems.stream().mapToInt(InventoryItem::getAvailableQuantity).min().orElse(0),
                locationStocks);
    }

    public Page<InventoryItemResponse> getLowStockItems(Pageable pageable) {
        Page<InventoryItem> lowStockPage = inventoryItemRepository.findLowStockItems(pageable);

        List<InventoryItemResponse> responses = lowStockPage.getContent().stream()
                .map(item -> new InventoryItemResponse(item))
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, lowStockPage.getTotalElements());
    }

    public InventoryItemResponse transferStock(Long fromItemId, Long toInventoryId, Integer quantity, String reason) {
        // Find source item
        InventoryItem sourceItem = this.findItemById(fromItemId);

        // Validate available quantity
        if (sourceItem.getAvailableQuantity() < quantity)
            throw new InsufficientStockException("Insufficient stock for transfer");

        // Find target inventory
        Inventory targetInventory = inventoryRepository.findById(toInventoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Target inventory not found"));

        // Find or create target item
        InventoryItem targetItem = inventoryItemRepository
                .findByInventoryIdAndProductIdAndSku(toInventoryId, sourceItem.getProductId(), sourceItem.getSku())
                .orElseGet(() -> {
                    // Create new item in target inventory
                    InventoryItem newItem = new InventoryItem(
                            targetInventory,
                            sourceItem.getProductId(),
                            sourceItem.getSku(),
                            0);
                    newItem.setMinStockLevel(sourceItem.getMinStockLevel());
                    newItem.setMaxStockLevel(sourceItem.getMaxStockLevel());
                    newItem.setReorderPoint(sourceItem.getReorderPoint());
                    return inventoryItemRepository.save(newItem);
                });

        // Perform transfer
        sourceItem.setQuantity(sourceItem.getQuantity() - quantity);
        targetItem.setQuantity(targetItem.getQuantity() + quantity);

        inventoryItemRepository.save(sourceItem);
        inventoryItemRepository.save(targetItem);

        // Record movements
        StockMovement outboundMovement = new StockMovement(
                sourceItem,
                MovementType.OUTBOUND,
                quantity,
                "Transfer to inventory: " + targetInventory.getName() + " - " + reason);
        stockMovementRepository.save(outboundMovement);

        StockMovement inboundMovement = new StockMovement(
                targetItem,
                MovementType.INBOUND,
                quantity,
                "Transfer from inventory: " + sourceItem.getInventory().getName() + " - " + reason);
        stockMovementRepository.save(inboundMovement);
        return new InventoryItemResponse(sourceItem);
    }

    public Page<StockMovement> getItemMovementHistory(Long inventoryId, Long productId, Pageable pageable) {
        return stockMovementRepository.findByInventoryItemIdAndProductId(inventoryId, productId, pageable);
    }

    public List<StockReservation> reserveStock(Long productId, String sku, int quantity, Long orderId) {
        // Find available inventory items
        List<InventoryItem> items = inventoryItemRepository
                .findByProductIdAndSkuAndActiveTrue(productId, sku)
                .stream()
                .sorted(Comparator.comparing(InventoryItem::getExpiryDate)) // FIFO or expiry-based
                .collect(Collectors.toList());

        if (items.isEmpty())
            throw new ResourceNotFoundException(
                    "No available stock found for product: " + productId + ", SKU: " + sku);

        int totalAvailable = items.stream().mapToInt(InventoryItem::getAvailableQuantity).sum();

        if (totalAvailable < quantity)
            throw new InsufficientStockException(
                    "Insufficient stock. Available: " + totalAvailable + ", Requested: " + quantity);

        // Reserve stock using appropriate strategy (FIFO, LIFO, etc.)
        int remainingToReserve = quantity;
        List<InventoryItem> updatedItems = new ArrayList<>();
        List<StockReservation> reservations = new ArrayList<>();

        for (InventoryItem item : items) {
            if (remainingToReserve <= 0)
                break;

            int availableInItem = item.getAvailableQuantity();
            int reserveFromItem = Math.min(availableInItem, remainingToReserve);

            item.setReservedQuantity(item.getReservedQuantity() + reserveFromItem);
            reservations.add(new StockReservation(item.getId(), orderId, reserveFromItem));

            updatedItems.add(item);
            remainingToReserve -= reserveFromItem;
        }

        // Save updated items
        inventoryItemRepository.saveAll(updatedItems);
        return stockReservationRepository.saveAll(reservations);
    }

    private InventoryItem findItemById(Long inventoryItemId) {
        return inventoryItemRepository.findById(inventoryItemId)
                .orElseThrow(() -> new ResourceNotFoundException("No item found with id: " + inventoryItemId));
    }
}
