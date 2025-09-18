package com.e_Commerce.product_service.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.e_Commerce.product_service.dtos.request.CategoryRequest;
import com.e_Commerce.product_service.dtos.response.CategoryResponse;
import com.e_Commerce.product_service.models.Category;
import com.e_Commerce.product_service.repositories.CategoryRepository;
import com.e_Commerce.product_service.repositories.ProductRepository;
import com.e_Commerce.product_service.services.CategoryService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryRepository categoryRepository;
    
    @Autowired
    ProductRepository productRepository;

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) 
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category with name " + request.getName() + " already exists");
        

        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        
        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parent category not found with id: " + request.getParentId()));
            category.setParent(parent);
        }
        
        return mapToCategoryResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found with id: " + id));
        return mapToCategoryResponse(category);
    }

    @Override
    @Transactional
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findByActiveTrue().stream()
                .map(this::mapToCategoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Page<CategoryResponse> getAllCategories(Pageable pageable) {
        return categoryRepository.findByActiveTrue(pageable)
                .map(this::mapToCategoryResponse);
    }

    @Override
    @Transactional
    public List<CategoryResponse> getRootCategories() {
        return categoryRepository.findRootCategories().stream()
                .map(this::mapToCategoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<CategoryResponse> getChildCategories(Long parentId) {
        return categoryRepository.findActiveChildrenByParentId(parentId).stream()
                .map(this::mapToCategoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found with id: " + id));
        
        if (request.getName() != null && !request.getName().equals(category.getName())) {
            if (categoryRepository.existsByNameAndIdNot(request.getName(), id)) 
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Category with name " + request.getName() + " already exists");
            
            category.setName(request.getName());
        }
        
        if (request.getDescription() != null) 
            category.setDescription(request.getDescription());
        
        
        if (request.getParentId() != null) {
            if (request.getParentId().equals(id)) 
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Category cannot be its own parent");
            
            
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parent category not found with id: " + request.getParentId()));
            
            category.setParent(parent);
        } else {
            category.setParent(null);
        }
        
        return mapToCategoryResponse(categoryRepository.save(category));
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found with id: " + id));
        
        if (hasProducts(id) > 0) 
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete category with associated products");
        
        
        if (categoryRepository.countChildrenByParentId(id) > 0) 
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete category with child categories");
        
        
        category.setActive(false);
        categoryRepository.save(category);
    }

    @Override
    @Transactional
    public long hasProducts(Long categoryId) {
        return productRepository.countByCategoryId(categoryId);
    }

    private CategoryResponse mapToCategoryResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        response.setActive(category.getActive());
        
        if (category.getParent() != null) {
            response.setParentId(category.getParent().getId());
            response.setParentName(category.getParent().getName());
        }
        
        response.setProductCount(productRepository.countByCategoryId(category.getId()));
        
        if (category.getChildren() != null && !category.getChildren().isEmpty()) {
            List<CategoryResponse> children = category.getChildren().stream()
                    .filter(Category::getActive)
                    .map(this::mapToCategoryResponse)
                    .collect(Collectors.toList());
            response.setChildren(children);
        }

        response.setCreatedAt(category.getCreatedAt());
        response.setUpdatedAt(category.getUpdatedAt());
        
        return response;
    }
}
