package com.e_Commerce.product_service.repositories;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.e_Commerce.product_service.models.Product;

import feign.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {  
    Optional<Product> findBySku(String sku);
    
    Optional<Product> findAllByIdAndSku(Long productId, String sku);
    
    Page<Product> findByActiveTrue(Pageable pageable);
    
    List<Product> findByActiveTrue();
    
    Page<Product> findByCategoryIdAndActiveTrue(Long categoryId, Pageable pageable);
    
    Page<Product> findByBrandIdAndActiveTrue(Long brandId, Pageable pageable);
    
    List<Product> findByIdIn(List<Long> ids);

    List<Product> findBySkuIn(List<String> ids);
    
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) AND p.active = true")
    Page<Product> searchByName(
        @Param("query") String query, 
        Pageable pageable
    );
        
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice AND p.active = true")
    Page<Product> findByPriceRange(
        @Param("minPrice") BigDecimal minPrice, 
        @Param("maxPrice") BigDecimal maxPrice, 
        Pageable pageable
    );
            
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.price BETWEEN :minPrice AND :maxPrice AND p.active = true")
    Page<Product> findByCategoryAndPriceRange(
        @Param("categoryId") Long categoryId,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        Pageable pageable
    );
                
    @Query("SELECT p FROM Product p WHERE " +
    "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
    "(:brandId IS NULL OR p.brand.id = :brandId) AND " +
    "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
    "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
    "p.active = true")
    Page<Product> findByFilters(
        @Param("categoryId") Long categoryId,
        @Param("brandId") Long brandId,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        Pageable pageable
    );
                    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId")
    long countByCategoryId(
        @Param("categoryId") Long categoryId
    );
    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.brand.id = :brandId")
    long countByBrandId(
        @Param("brandId") Long brandId
    );
    
    boolean existsBySku(String sku);
    
    boolean existsByIdAndSku(Long productId, String sku);
    
    boolean existsBySkuAndIdNot(String sku, Long id);

}
