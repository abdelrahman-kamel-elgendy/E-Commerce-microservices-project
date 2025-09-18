package com.e_Commerce.product_service.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.e_Commerce.product_service.dtos.request.BrandRequest;
import com.e_Commerce.product_service.dtos.response.BrandResponse;

public interface BrandService {
    BrandResponse createBrand(BrandRequest request);
    BrandResponse getBrandById(Long id);
    List<BrandResponse> getAllBrands();
    Page<BrandResponse> getAllBrands(Pageable pageable);
    BrandResponse updateBrand(Long id, BrandRequest request);
    void deleteBrand(Long id);
    long hasProducts(Long brandId);
}