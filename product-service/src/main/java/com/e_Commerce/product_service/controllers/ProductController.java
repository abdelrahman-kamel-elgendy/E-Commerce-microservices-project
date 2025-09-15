package com.e_Commerce.product_service.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.e_Commerce.product_service.dtos.ApiResponse;
import com.e_Commerce.product_service.dtos.ProductDto;
import com.e_Commerce.product_service.models.Product;
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

    @PostMapping
    public ResponseEntity<ApiResponse<Product>> createProduct(@Valid @RequestBody ProductDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            new ApiResponse<Product>(
                true, 
                "Product created successfully", 
                productService.createProduct(dto)
            )
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> updateProduct(@PathVariable Long id,  @Valid @RequestBody ProductDto dto) {
        return ResponseEntity.ok(
            new ApiResponse<Product>(
                true, 
                "Product updated successfully", 
                productService.updateProduct(id, dto)
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

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<Product>>> getAllActiveProducts() {
        return ResponseEntity.ok(new ApiResponse<List<Product>>(
            true, 
            "Products retrieved successfully", 
            productService.getAllActiveProducts()
            )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(
            new ApiResponse<Product>(
                true, 
                "Product retrieved successfully", 
                productService.getProductById(id)
            )
        );
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> deleteProduct(@PathVariable Long id) {
        return ResponseEntity.ok(
            new ApiResponse<Product> (
                true,
                "Product deleted successfully",
                productService.deleteProduct(id)
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
