package com.e_Commerce.product_service.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.e_Commerce.product_service.exception.BadRequestException;
import com.e_Commerce.product_service.exception.ResourceNotFoundException;
import com.e_Commerce.product_service.exception.ApiException;

import com.e_Commerce.product_service.dtos.request.BrandRequest;
import com.e_Commerce.product_service.dtos.response.BrandResponse;
import com.e_Commerce.product_service.models.Brand;
import com.e_Commerce.product_service.repositories.BrandRepository;
import com.e_Commerce.product_service.repositories.ProductRepository;
import com.e_Commerce.product_service.services.BrandService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class BrandService {

    @Autowired
    BrandRepository brandRepository;
    @Autowired
    ProductRepository productRepository;

    
    public BrandResponse createBrand(BrandRequest request) {
        if (brandRepository.existsByName(request.getName())) {
            throw new BadRequestException("Brand with name " + request.getName() + " already exists");
        }

        Brand brand = new Brand();
        brand.setName(request.getName());
        brand.setDescription(request.getDescription());
        brand.setLogoUrl(request.getLogoUrl());
        
        Brand savedBrand = brandRepository.save(brand);
        return mapToBrandResponse(savedBrand);
    }

    
    @Transactional
    public BrandResponse getBrandById(Long id) {
    Brand brand = brandRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + id));
        return mapToBrandResponse(brand);
    }

    
    @Transactional
    public List<BrandResponse> getAllBrands() {
        return brandRepository.findByActiveTrue().stream()
                .map(this::mapToBrandResponse)
                .collect(Collectors.toList());
    }

    
    @Transactional
    public Page<BrandResponse> getAllBrands(Pageable pageable) {
        return brandRepository.findByActiveTrue(pageable)
                .map(this::mapToBrandResponse);
    }

    
    public BrandResponse updateBrand(Long id, BrandRequest request) {
    Brand brand = brandRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + id));
        
        if (request.getName() != null && !request.getName().equals(brand.getName())) {
            if (brandRepository.existsByNameAndIdNot(request.getName(), id)) {
                throw new ApiException(HttpStatus.CONFLICT, "Brand with name " + request.getName() + " already exists");
            }
            brand.setName(request.getName());
        }
        
        if (request.getDescription() != null) {
            brand.setDescription(request.getDescription());
        }
        
        if (request.getLogoUrl() != null) {
            brand.setLogoUrl(request.getLogoUrl());
        }
        
        Brand updatedBrand = brandRepository.save(brand);
        return mapToBrandResponse(updatedBrand);
    }

    
    public void deleteBrand(Long id) {
    Brand brand = brandRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + id));
        
        if (hasProducts(id) > 0) 
            throw new BadRequestException("Cannot delete brand with associated products");
        
        
        brand.setActive(false);
        brandRepository.save(brand);
    }

    
    @Transactional
    public long hasProducts(Long brandId) {
        return productRepository.countByBrandId(brandId);
    }

    private BrandResponse mapToBrandResponse(Brand brand) {
        BrandResponse response = new BrandResponse();
        response.setId(brand.getId());
        response.setName(brand.getName());
        response.setDescription(brand.getDescription());
        response.setLogoUrl(brand.getLogoUrl());
        response.setActive(brand.getActive());
        response.setProductCount(productRepository.countByBrandId(brand.getId()));
        return response;
    }
}