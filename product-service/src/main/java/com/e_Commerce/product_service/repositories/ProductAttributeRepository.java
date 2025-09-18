package com.e_Commerce.product_service.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.e_Commerce.product_service.models.ProductAttribute;

import feign.Param;

public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, Long> {
    
    List<ProductAttribute> findByProductId(Long productId);
    
    Optional<ProductAttribute> findByProductIdAndName(Long productId, String name);
    
    List<ProductAttribute> findByProductIdAndNameIn(Long productId, List<String> names);
    
    @Modifying
    @Query("DELETE FROM ProductAttribute pa WHERE pa.product.id = :productId")
    void deleteByProductId(@Param("productId") Long productId);
    
    @Modifying
    @Query("DELETE FROM ProductAttribute pa WHERE pa.product.id = :productId AND pa.name = :name")
    void deleteByProductIdAndName(@Param("productId") Long productId, @Param("name") String name);
    
    boolean existsByProductIdAndName(Long productId, String name);
}
