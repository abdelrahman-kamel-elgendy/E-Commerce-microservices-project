package com.e_Commerce.product_service.controllers;

import java.math.BigDecimal;
import java.util.List;

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
import com.e_Commerce.product_service.dtos.response.ApiResponse;
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
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            new ApiResponse<ProductResponse>(
                true,
                "Product created successfully",
                productService.createProduct(request)
            )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(
            new ApiResponse<ProductResponse>(
                true,
                "Product retrieved successfully",
                productService.getProductById(id)
            )
        );
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductBySku(@PathVariable String sku) {
        return ResponseEntity.ok(
            new ApiResponse<ProductResponse>(
                true,
                "Product retrieved successfully",
                productService.getProductBySku(sku)
            )
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getAllProducts(Pageable pageable) {
        return ResponseEntity.ok(
            new ApiResponse<Page<ProductResponse>>(
                true,
                "Products retrieved successfully",
                productService.getAllProducts(pageable)
            )
        );
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getProductsByCategory(@PathVariable Long categoryId, Pageable pageable) {
        return ResponseEntity.ok(
            new ApiResponse<Page<ProductResponse>>(
                true,
                "Products retrieved successfully",
                productService.getProductsByCategory(categoryId, pageable)
            )
        );
    }

    @GetMapping("/brand/{brandId}")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getProductsByBrand(@PathVariable Long brandId, Pageable pageable) {
       return ResponseEntity.ok(
            new ApiResponse<Page<ProductResponse>>(
                true,
                "Products retrieved successfully",
                productService.getProductsByBrand(brandId, pageable)
            )
        );
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> searchProducts(@RequestParam String query, Pageable pageable) {
        return ResponseEntity.ok(
            new ApiResponse<Page<ProductResponse>>(
                true,
                "Products retrieved successfully",
                productService.searchProducts(query, pageable)
            )
        );
    }

    @GetMapping("/price-range")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getProductsByPriceRange(
        @RequestParam BigDecimal minPrice,
        @RequestParam BigDecimal maxPrice,
        Pageable pageable
    ) {
        return ResponseEntity.ok(
            new ApiResponse<Page<ProductResponse>>(
                true,
                "Products retrieved successfully",
                productService.getProductsByPriceRange(minPrice, maxPrice, pageable)
            )
        );
    }

    @GetMapping("/category/{categoryId}/price-range")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getProductsByCategoryAndPriceRange(
        @PathVariable Long categoryId,
        @RequestParam BigDecimal minPrice,
        @RequestParam BigDecimal maxPrice,
        Pageable pageable
    ) {
        return ResponseEntity.ok(
            new ApiResponse<Page<ProductResponse>>(
                true,
                "Products retrieved successfully",
                productService.getProductsByCategoryAndPriceRange(categoryId, minPrice, maxPrice, pageable)
            )
        );
    }

    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getProductsByFilters(
        @RequestParam(required = false) Long categoryId,
        @RequestParam(required = false) Long brandId,
        @RequestParam(required = false) BigDecimal minPrice,
        @RequestParam(required = false) BigDecimal maxPrice,
        Pageable pageable
    ) {
        return ResponseEntity.ok(
            new ApiResponse<Page<ProductResponse>>(
                true,
                "Products retrieved successfully",
                productService.getProductsByFilters(categoryId, brandId, minPrice, maxPrice, pageable)
            )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
        @PathVariable Long id, 
        @Valid @RequestBody ProductUpdateRequest request
    ) {
        return ResponseEntity.ok(
            new ApiResponse<ProductResponse>(
                true,
                "Product updated successfully",
                productService.updateProduct(id, request)
            )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/batch")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getProductsByIds(@RequestBody List<Long> ids) {
        return ResponseEntity.ok(
            new ApiResponse<List<ProductResponse>>(
                true,
                "Products retrieved successfully",
                productService.getProductsByIds(ids)
            )
        );
    }

    @GetMapping("/category/{categoryId}/count")
    public ResponseEntity<ApiResponse<Long>> countProductsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(
            new ApiResponse<Long>(
                true,
                "Products count retrieved successfully",
                productService.countProductsByCategory(categoryId)
            )
        );
    }

    @GetMapping("/brand/{brandId}/count")
    public ResponseEntity<ApiResponse<Long>> countProductsByBrand(@PathVariable Long brandId) {
        return ResponseEntity.ok(
            new ApiResponse<Long>(
                true,
                "Products count retrieved successfully",
                productService.countProductsByBrand(brandId)
            )
        );
    }
}