package com.e_Commerce.product_service.controllers;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.e_Commerce.product_service.dtos.request.ProductCreateRequest;
import com.e_Commerce.product_service.dtos.request.ProductUpdateRequest;
import com.e_Commerce.product_service.dtos.response.ProductResponse;
import com.e_Commerce.product_service.services.ProductService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                productService.createProduct(request)
        );
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getAllProducts(Pageable pageable) {
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    @GetMapping("/id")
    public ResponseEntity<ProductResponse> getProductById(@RequestParam Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> checkProductExistence(
        @RequestParam String sku
    ) {
        return ResponseEntity.ok(productService.existsBySku(sku));
    }

    @GetMapping("/category")
    public ResponseEntity<Page<ProductResponse>> getProductsByCategory(
        @RequestParam Long categoryId, 
        Pageable pageable
    ) {
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId, pageable));
    }

    @GetMapping("/brand")
    public ResponseEntity<Page<ProductResponse>> getProductsByBrand(
        @RequestParam Long brandId, 
        Pageable pageable
    ) {
       return ResponseEntity.ok(productService.getProductsByBrand(brandId, pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponse>> searchProducts(
        @RequestParam String query, 
        Pageable pageable
    ) {
        return ResponseEntity.ok(productService.searchProducts(query, pageable));
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<ProductResponse>> getProductsByFilters(
        @RequestParam(required = false) Long categoryId,
        @RequestParam(required = false) Long brandId,
        @RequestParam(required = false) BigDecimal minPrice,
        @RequestParam(required = false) BigDecimal maxPrice,
        Pageable pageable
    ) {
        return ResponseEntity.ok(
            productService.getProductsByFilters(
                categoryId, 
                brandId, 
                minPrice, 
                maxPrice, 
                pageable
            )
        
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
        @PathVariable Long id, 
        @Valid @RequestBody ProductUpdateRequest request
    ) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}