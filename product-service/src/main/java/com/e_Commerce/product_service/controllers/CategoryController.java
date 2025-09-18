package com.e_Commerce.product_service.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.e_Commerce.product_service.dtos.request.CategoryRequest;
import com.e_Commerce.product_service.dtos.response.ApiResponse;
import com.e_Commerce.product_service.dtos.response.CategoryResponse;
import com.e_Commerce.product_service.services.CategoryService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    
    
    @Autowired
    CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            new ApiResponse<CategoryResponse>(
                true,
                "Category created successfully",
                categoryService.createCategory(request)
            )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(
            new ApiResponse<CategoryResponse>(
                true,
                "Category retrieved successfully",
                categoryService.getCategoryById(id)
            )
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        return ResponseEntity.ok(
            new ApiResponse<List<CategoryResponse>>(
                true,
                "Categories retrieved successfully",
                categoryService.getAllCategories()
            )
        );
    }

    @GetMapping("/page")
    public ResponseEntity<ApiResponse<Page<CategoryResponse>>> getAllCategories(Pageable pageable) {
        return ResponseEntity.ok(
            new ApiResponse<Page<CategoryResponse>>(
                true,
                "Categories retrieved successfully",
                categoryService.getAllCategories(pageable)
            )
        );
    }

    @GetMapping("/roots")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getRootCategories() {
        return ResponseEntity.ok(
            new ApiResponse<List<CategoryResponse>>(
                true,
                "Categories retrieved successfully",
                categoryService.getRootCategories()
            )
        );
    }

    @GetMapping("/{parentId}/children")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getChildCategories(@PathVariable Long parentId) {
        return ResponseEntity.ok(
            new ApiResponse<List<CategoryResponse>>(
                true,
                "Categories retrieved successfully",
                categoryService.getChildCategories(parentId)
            )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
        @PathVariable Long id, 
        @Valid @RequestBody CategoryRequest request
    ) {
        return ResponseEntity.ok(
            new ApiResponse<CategoryResponse>(
                true,
                "Category updated successfully",
                categoryService.updateCategory(id, request)
            )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/has-products")
    public ResponseEntity<ApiResponse<Long>> hasProducts(@PathVariable Long id) {
        long hasProducts = categoryService.hasProducts(id);
        return ResponseEntity.ok(
            new ApiResponse<Long> (
                true,
                hasProducts > 0 ? "Category has products":"Category has no products",
                hasProducts 
            )
        );
    }
}
