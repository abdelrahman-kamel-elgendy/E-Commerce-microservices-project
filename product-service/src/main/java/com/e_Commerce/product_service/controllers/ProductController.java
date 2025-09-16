package com.e_Commerce.product_service.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.e_Commerce.product_service.dtos.Inventory;
import com.e_Commerce.product_service.dtos.ProductDetails;
import com.e_Commerce.product_service.dtos.ProductDto;
import com.e_Commerce.product_service.feigns.InventoryFeign;
import com.e_Commerce.product_service.models.Product;
import com.e_Commerce.product_service.res.ApiResponse;
import com.e_Commerce.product_service.services.CategoryService;
import com.e_Commerce.product_service.services.ProductService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @Autowired
    ProductService productService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    InventoryFeign inventoryFeign;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductDetails>> createProduct(@Valid @RequestBody ProductDto dto) {
        Product product = productService.createProduct(dto);
        ResponseEntity<ApiResponse<Inventory>> inventoryResponse = inventoryFeign.createInventory(product.getId(), dto.getQuantity()); 
        
        if(!inventoryResponse.getBody().isSuccess())
            productService.deleteProduct(product.getId());  
        
        return ResponseEntity.status(HttpStatus.CREATED).body(
            new ApiResponse<ProductDetails>(
                true, 
                "Product created successfully", 
                new ProductDetails(
                    product.getId(),
                    product.getName(),
                    product.getName(), 
                    product.getPrice(), 
                    categoryService.getCategoryById(product.getCategoryId()).getName(),
                    inventoryResponse.getBody().getData().getQuantity(),
                    product.getImageUrl()
                )
            )
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDetails>> updateProduct(@PathVariable Long id,  @Valid @RequestBody ProductDto dto) {
        Product product = productService.updateProduct(id, dto);
        Inventory inventory = inventoryFeign.updateStok(id, dto.getQuantity()).getBody().getData();

        return ResponseEntity.ok(
            new ApiResponse<ProductDetails>(
                true, 
                "Product updated successfully", 
                new ProductDetails(
                    product.getId(), 
                    product.getName(), 
                    product.getDescription(), 
                    product.getPrice(), 
                    categoryService.getCategoryById(product.getCategoryId()).getName(),
                    inventory.getQuantity(),
                    product.getImageUrl()
                )
            )
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Product>>> getAllProducts() {
        return ResponseEntity.ok(new ApiResponse<List<Product>>(
            true, 
            "Products retrieved successfully", 
            productService.getAllProducts()
            )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDetails>> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        Inventory inventory = inventoryFeign.getInventoryByProductId(product.getId()).getBody().getData();

        return ResponseEntity.ok(
            new ApiResponse<ProductDetails>(
                true, 
                "Product retrieved successfully", 
                new ProductDetails(
                    product.getId(), 
                    product.getName(), 
                    product.getDescription(), 
                    product.getPrice(), 
                    categoryService.getCategoryById(product.getCategoryId()).getName(),
                    inventory.getQuantity(),
                    product.getImageUrl()
                )
            )
        );
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> deleteProduct(@PathVariable Long id) {
        inventoryFeign.deleteInventory(id);

        return ResponseEntity.ok(
            new ApiResponse<Product> (
                true,
                "Product deleted successfully",
                productService.deleteProduct(id)
            )
        );
    }

    
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<Product>>> getAllActiveProducts() {
        return ResponseEntity.ok(new ApiResponse<List<Product>>(
            true, 
            "Products retrieved successfully", 
            productService.getAllActiveProducts()
            )
        );
    }

    @PutMapping("/deactivate/{id}")
    public ResponseEntity<ApiResponse<Product>> deactivateProduct(@PathVariable Long id) {
        return ResponseEntity.ok(
            new ApiResponse<Product> (
                true,
                "Product deactivated successfully",
                productService.ProductActivation(id, false)
            )
        );
    }

    @PutMapping("/activate/{id}")
    public ResponseEntity<ApiResponse<Product>> activateProduct(@PathVariable Long id) {
        return ResponseEntity.ok(
            new ApiResponse<Product> (
                true,
                "Product activated successfully",
                productService.ProductActivation(id, true)
            )
        );
    }
}
