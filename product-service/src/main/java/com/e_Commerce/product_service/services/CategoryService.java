package com.e_Commerce.product_service.services;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.e_Commerce.product_service.dtos.request.CategoryRequest;
import com.e_Commerce.product_service.dtos.response.CategoryResponse;

public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest request);
    CategoryResponse getCategoryById(Long id);
    List<CategoryResponse> getAllCategories();
    Page<CategoryResponse> getAllCategories(Pageable pageable);
    List<CategoryResponse> getRootCategories();
    List<CategoryResponse> getChildCategories(Long parentId);
    CategoryResponse updateCategory(Long id, CategoryRequest request);
    void deleteCategory(Long id);
    long hasProducts(Long categoryId);
}
