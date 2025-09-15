package com.e_Commerce.product_service.services;


import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.e_Commerce.product_service.dtos.CategoryDto;
import com.e_Commerce.product_service.models.Category;
import com.e_Commerce.product_service.repositories.CategoryRepository;

@Service
public class CategoryService {
    
    @Autowired
    CategoryRepository categoryRepository;

    public Category getCategoruByName(String name) {
        return categoryRepository.findByName(name)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category with name " + name + " not found"));
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category with id " + id + " not found"));
    }

    public Category createCategory(CategoryDto dto) {
        if (categoryRepository.existsByName(dto.getName())) 
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category with the same name already exists");

        Category category = new Category();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setImageUrl(dto.getImageUrl());

        return categoryRepository.save(category);
    }

    public Category updateCategory(Long id, CategoryDto dto) {
        Category category = this.getCategoruByName(dto.getName());

        if(category != null && category.getId()!=id)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category with the same name already exists");

        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setImageUrl(dto.getImageUrl());

        category.setUpdatedAt(Instant.now());

        return categoryRepository.save(category);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<Category> getAllActiveCategories() {
        return categoryRepository.findAllByActive(true);
    }

    public Category deleteCategory(Long id) {
        Category category = this.getCategoryById(id);
        categoryRepository.delete(category);
        return category;
    }

    public Category categoryActivation(Long id, boolean activation) {
        Category category = this.getCategoryById(id);
        category.setActive(activation);
        category.setUpdatedAt(Instant.now());
        return categoryRepository.save(category);
    }
}
