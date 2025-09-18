package com.e_Commerce.product_service.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.e_Commerce.product_service.models.ProductImage;

import feign.Param;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    
    List<ProductImage> findByProductId(Long productId);
    
    List<ProductImage> findByProductIdOrderByDisplayOrderAsc(Long productId);
    
    Optional<ProductImage> findByProductIdAndId(Long productId, Long imageId);
    
    @Query("SELECT pi FROM ProductImage pi WHERE pi.product.id = :productId AND pi.displayOrder = :displayOrder")
    Optional<ProductImage> findByProductIdAndDisplayOrder(@Param("productId") Long productId, @Param("displayOrder") Integer displayOrder);
    
    boolean existsByProductIdAndImageUrl(Long productId, String imageUrl);
    
    @Modifying
    @Query("DELETE FROM ProductImage pi WHERE pi.product.id = :productId")
    void deleteByProductId(@Param("productId") Long productId);
    
    @Modifying
    @Query("DELETE FROM ProductImage pi WHERE pi.product.id = :productId AND pi.id = :imageId")
    void deleteByProductIdAndImageId(@Param("productId") Long productId, @Param("imageId") Long imageId);
    
    long countByProductId(Long productId);
}
