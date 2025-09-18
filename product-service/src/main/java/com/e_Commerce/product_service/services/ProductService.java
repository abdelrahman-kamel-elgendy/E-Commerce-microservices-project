package com.e_Commerce.product_service.services;

import java.math.BigDecimal;
import java.util.List;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.e_Commerce.product_service.dtos.request.ProductCreateRequest;
import com.e_Commerce.product_service.dtos.request.ProductUpdateRequest;
import com.e_Commerce.product_service.dtos.response.ProductResponse;

public interface ProductService {
    ProductResponse createProduct(ProductCreateRequest request);
    ProductResponse getProductById(Long id);
    ProductResponse getProductBySku(String sku);
    Page<ProductResponse> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    Page<ProductResponse> getProductsByCategoryAndPriceRange(Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    Page<ProductResponse> getAllProducts(Pageable pageable);
    Page<ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable);
    Page<ProductResponse> getProductsByBrand(Long brandId, Pageable pageable);
    Page<ProductResponse> searchProducts(String query, Pageable pageable);
    ProductResponse updateProduct(Long id, ProductUpdateRequest request);
    void deleteProduct(Long id);
    List<ProductResponse> getProductsByIds(List<Long> ids);
    long countProductsByCategory(Long categoryId);
     public long countProductsByBrand(Long brandId);
     Page<ProductResponse> getProductsByFilters(Long categoryId, Long brandId, BigDecimal minPrice, BigDecimal maxPrice,
            Pageable pageable);
}