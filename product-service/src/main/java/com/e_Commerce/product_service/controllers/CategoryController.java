package com.e_Commerce.product_service.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.e_Commerce.product_service.dtos.CategoryDto;
import com.e_Commerce.product_service.models.Category;
import com.e_Commerce.product_service.res.ApiResponse;
import com.e_Commerce.product_service.services.CategoryService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PatchMapping;
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
    public ResponseEntity<ApiResponse<Category>> createCategory(@Valid @RequestBody CategoryDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            new ApiResponse<Category>(
                true, 
                "Category created successfully", 
                categoryService.createCategory(dto)
            )
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Category>> updateCategory(@PathVariable Long id,  @Valid @RequestBody CategoryDto dto) {
        return ResponseEntity.ok(
            new ApiResponse<Category>(
                true, 
                "Category updated successfully", 
                categoryService.updateCategory(id, dto)
            )
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Category>>> getAllCategories() {
        return ResponseEntity.ok(new ApiResponse<List<Category>>(
            true, 
            "Categories retrieved successfully", 
            categoryService.getAllCategories()
            )
        );
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<Category>>> getAllActiveCategories() {
        return ResponseEntity.ok(new ApiResponse<List<Category>>(
            true, 
            "Categories retrieved successfully", 
            categoryService.getAllActiveCategories()
            )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Category>> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(
            new ApiResponse<Category>(
                true, 
                "Category retrieved successfully", 
                categoryService.getCategoryById(id)
            )
        );
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Category>> deleteCategory(@PathVariable Long id) {
        return ResponseEntity.ok(
            new ApiResponse<Category> (
                true,
                "Category deleted successfully",
                categoryService.deleteCategory(id)
            )
        );
    }

    @PutMapping("/deactivate/{id}")
    public ResponseEntity<ApiResponse<Category>> deactivateCategory(@PathVariable Long id) {
        return ResponseEntity.ok(
            new ApiResponse<Category> (
                true,
                "Category deactivated successfully",
                categoryService.categoryActivation(id, false)
            )
        );
    }

    @PutMapping("/activate/{id}")
    public ResponseEntity<ApiResponse<Category>> activateCategory(@PathVariable Long id) {
        return ResponseEntity.ok(
            new ApiResponse<Category> (
                true,
                "Category activated successfully",
                categoryService.categoryActivation(id, true)
            )
        );
    }
}
