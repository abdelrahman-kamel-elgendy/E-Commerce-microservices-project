package com.e_Commerce.product_service.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.e_Commerce.product_service.models.Brand;

import feign.Param;

public interface BrandRepository extends JpaRepository<Brand, Long> {
    Optional<Brand> findByName(String name);
    
    Page<Brand> findByActiveTrue(Pageable pageable);
    
    List<Brand> findByActiveTrue();
    
    @Query("SELECT b FROM Brand b WHERE LOWER(b.name) LIKE LOWER(CONCAT('%', :query, '%')) AND b.active = true")
    Page<Brand> searchByName(@Param("query") String query, Pageable pageable);
    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.brand.id = :brandId")
    long countProductsByBrandId(@Param("brandId") Long brandId);
    
    boolean existsByName(String name);
    
    boolean existsByNameAndIdNot(String name, Long id);
}
