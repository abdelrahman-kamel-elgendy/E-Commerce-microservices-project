package com.e_Commerce.inventory_service.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.e_Commerce.inventory_service.dto.request.InventoryRequest;
import com.e_Commerce.inventory_service.dto.response.InventoryResponse;
import com.e_Commerce.inventory_service.dto.response.ProductResponse;
import com.e_Commerce.inventory_service.exceptions.InsufficientInventoryException;
import com.e_Commerce.inventory_service.exceptions.InvalidRequestException;
import com.e_Commerce.inventory_service.exceptions.ResourceAlreadyExistsException;
import com.e_Commerce.inventory_service.exceptions.ResourceNotFoundException;
import com.e_Commerce.inventory_service.feigns.ProductServiceClient;
import com.e_Commerce.inventory_service.models.Inventory;
import com.e_Commerce.inventory_service.models.MovementType;
import com.e_Commerce.inventory_service.models.StockMovement;
import com.e_Commerce.inventory_service.models.StockReservation;
import com.e_Commerce.inventory_service.models.ReservationStatus;
import com.e_Commerce.inventory_service.repositories.InventoryRepository;
import com.e_Commerce.inventory_service.repositories.StockMovementRepository;
import com.e_Commerce.inventory_service.repositories.StockReservationRepository;

@Service
public class InventoryService {
    @Autowired
    InventoryRepository repository;

    @Autowired
    StockMovementRepository movementRepository;

    @Autowired
    StockReservationRepository reservationRepository;

    @Autowired
    ProductServiceClient productServiceClient;

    public InventoryResponse createInventory(InventoryRequest dto) {
        ProductResponse product = this.getProductById(dto.getProductId());

        if (repository.existsByProductIdAndSkuCode(dto.getProductId(), dto.getSkuCode()))
            throw new ResourceAlreadyExistsException("Inventory already exists for productId=" + dto.getProductId() + " sku=" + dto.getSkuCode());

        Inventory inventory = new Inventory(dto.getProductId(), dto.getSkuCode(), dto.getQuantity());
        inventory.setQuantity(dto.getQuantity());
        if (dto.getMinStockLevel() != null) inventory.setMinStockLevel(dto.getMinStockLevel());
        if (dto.getMaxStockLevel() != null) inventory.setMaxStockLevel(dto.getMaxStockLevel());
        if (dto.getReorderPoint() != null) inventory.setReorderPoint(dto.getReorderPoint());
        if (dto.getLocation() != null) inventory.setLocation(dto.getLocation());
        if (dto.getBatchNumber() != null) inventory.setBatchNumber(dto.getBatchNumber());
        if (dto.getExpiryDate() != null) inventory.setExpiryDate(dto.getExpiryDate());
        if (dto.getReservedQuantity() != null) inventory.setReservedQuantity(dto.getReservedQuantity());
        return new InventoryResponse(repository.save(inventory), product);
    }

    public Page<InventoryResponse> getAllInventories(Pageable pageable) {
    Page<Inventory> page = repository.findAll(pageable);
    List<InventoryResponse> responseList = page.getContent()
            .stream()
            .map(inventory -> {
                ProductResponse product = this.getProductById(inventory.getProductId());
                return new InventoryResponse(inventory, product);
            })
            .collect(Collectors.toList());

    return new PageImpl<>(responseList, page.getPageable(), page.getTotalElements());
}

    public Inventory getPtoductInventory(Long productId, String sku) {        
        return this.getInventoryByProductIdAndSku(productId, sku);
    }

    public Inventory increaseInventory(Long productId, String sku, int quantity) {
        if(quantity <= 0) 
            throw new InvalidRequestException("Quantity must be positive");
        
            Inventory inventory = this.getInventoryByProductIdAndSku(productId, sku);
        inventory.setQuantity(inventory.getQuantity() + quantity);
        return repository.save(inventory);
    }

    public Inventory decreaseInventory(Long productId, String sku, int quantity) {
        if(quantity <= 0) 
            throw new InvalidRequestException("Quantity must be positive");
        
            Inventory inventory = this.getInventoryByProductIdAndSku(productId, sku);
        if(inventory.getQuantity() < quantity)
            throw new InsufficientInventoryException("Requested " + quantity + " but only " + inventory.getQuantity() + " available");
        
        inventory.setQuantity(inventory.getQuantity() - quantity);
        return repository.save(inventory);
    }

    public Boolean checkPtoductQuantity(Long productId, String sku, int quantity) {
        if(quantity <= 0) 
            throw new InvalidRequestException("Quantity must be positive");
        Inventory inventory = this.getInventoryByProductIdAndSku(productId, sku);

        return inventory.getQuantity() > quantity;
    }

    public Page<InventoryResponse> getLowStockItems() {
        List<Inventory> all = repository.findAll();
        List<InventoryResponse> low = all.stream()
                .filter(inv -> inv.getQuantity() != null && inv.getMinStockLevel() != null && inv.getQuantity() <= inv.getMinStockLevel())
                .map(inv -> new InventoryResponse(inv, this.getProductById(inv.getProductId())))
                .collect(Collectors.toList());

        return new PageImpl<>(low);
    }

    public Inventory adjustStock(Long id, Integer quantity, String reason, MovementType movementType) {
        if (quantity == null || quantity == 0) throw new InvalidRequestException("quantity must be positive");

        Inventory inventory = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found id: " + id));

        if (movementType == MovementType.OUTBOUND) {
            if (inventory.getQuantity() < quantity)
                throw new InsufficientInventoryException("Not enough stock to decrease by " + quantity);

            inventory.setQuantity(inventory.getQuantity() - quantity);
        } else 
            inventory.setQuantity(inventory.getQuantity() + quantity);
        

        Inventory saved = repository.save(inventory);

        StockMovement movement = new StockMovement(saved.getId(), movementType, quantity, reason);
        movementRepository.save(movement);

        return saved;
    }

    public Inventory reserveStock(Long inventoryId, Integer quantity, Long orderId) {
        if (quantity == null || quantity <= 0) 
            throw new InvalidRequestException("quantity must be positive");

        Inventory inventory = repository.findById(inventoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found id=" + inventoryId));

        Integer reserved = reservationRepository.sumReservedQuantityByInventoryId(inventoryId);
        if (reserved == null) reserved = 0;

        int available = inventory.getQuantity() - reserved;
        if (available < quantity) throw new InsufficientInventoryException("Requested " + quantity + " but only " + available + " available for reservation");

        StockReservation res = new StockReservation(inventoryId, orderId, quantity);
        reservationRepository.save(res);

        StockMovement movement = new StockMovement(inventoryId, MovementType.RESERVATION, quantity, "Reservation for order " + orderId);
        movementRepository.save(movement);

        inventory.setReservedQuantity((inventory.getReservedQuantity() == null ? 0 : inventory.getReservedQuantity()) + quantity);
        Inventory saved = repository.save(inventory);

        return saved;
    }

    public Inventory releaseReservedStock(Long inventoryId, Integer quantity, Long orderId) {
        if (quantity == null || quantity <= 0) 
            throw new InvalidRequestException("quantity must be positive");

        Inventory inventory = repository.findById(inventoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found id: " + inventoryId));

        StockReservation reservation = reservationRepository.findByOrderIdAndInventoryId(orderId, inventoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found for order: " + orderId + " inventory: " + inventoryId));

        if (reservation.getQuantity() < quantity) 
            throw new InvalidRequestException("Release quantity greater than reserved quantity");

        reservation.setQuantity(reservation.getQuantity() - quantity);
        if (reservation.getQuantity() == 0) 
            reservation.setStatus(ReservationStatus.CANCELLED);

        reservationRepository.save(reservation);

        StockMovement movement = new StockMovement(inventoryId, MovementType.RELEASE, quantity, "Release for order " + orderId);
        movementRepository.save(movement);

        inventory.setReservedQuantity((inventory.getReservedQuantity() == null ? 0 : inventory.getReservedQuantity()) - quantity);
        Inventory saved = repository.save(inventory);

        return saved;
    }


    private ProductResponse getProductById(Long productId) {
        return productServiceClient.getProductById(productId).getBody();
    }

    private Inventory getInventoryByProductIdAndSku(Long productId, String sku) {
        return repository.findByProductIdAndSkuCode(productId, sku)
            .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for productId=" + productId + " sku=" + sku));
    }

}