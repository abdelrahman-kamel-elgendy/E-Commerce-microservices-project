package com.e_Commerce.product_service.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.e_Commerce.product_service.models.Category;

import feign.Param;

public interface CategoryRepository extends JpaRepository<Category, Long> { 
    Optional<Category> findByName(String name);
    
    List<Category> findByParentIsNull();
    
    List<Category> findByParentId(Long parentId);
    
    Page<Category> findByActiveTrue(Pageable pageable);
    
    List<Category> findByActiveTrue();
    
    @Query("SELECT c FROM Category c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')) AND c.active = true")
    Page<Category> searchByName(@Param("query") String query, Pageable pageable);
    
    @Query("SELECT c FROM Category c WHERE c.parent IS NULL AND c.active = true")
    List<Category> findRootCategories();
    
    @Query("SELECT c FROM Category c WHERE c.parent.id = :parentId AND c.active = true")
    List<Category> findActiveChildrenByParentId(@Param("parentId") Long parentId);
    
    @Query("SELECT COUNT(c) FROM Category c WHERE c.parent.id = :parentId")
    long countChildrenByParentId(@Param("parentId") Long parentId);
    
    boolean existsByName(String name);
    
    boolean existsByNameAndIdNot(String name, Long id);
} 
