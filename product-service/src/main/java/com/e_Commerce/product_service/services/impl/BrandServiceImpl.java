package com.e_Commerce.product_service.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.e_Commerce.product_service.dtos.request.BrandRequest;
import com.e_Commerce.product_service.dtos.response.BrandResponse;
import com.e_Commerce.product_service.models.Brand;
import com.e_Commerce.product_service.repositories.BrandRepository;
import com.e_Commerce.product_service.repositories.ProductRepository;
import com.e_Commerce.product_service.services.BrandService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class BrandServiceImpl implements BrandService {

    @Autowired
    BrandRepository brandRepository;
    @Autowired
    ProductRepository productRepository;

    @Override
    public BrandResponse createBrand(BrandRequest request) {
        if (brandRepository.existsByName(request.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Brand with name " + request.getName() + " already exists");
        }

        Brand brand = new Brand();
        brand.setName(request.getName());
        brand.setDescription(request.getDescription());
        brand.setLogoUrl(request.getLogoUrl());
        
        Brand savedBrand = brandRepository.save(brand);
        return mapToBrandResponse(savedBrand);
    }

    @Override
    @Transactional
    public BrandResponse getBrandById(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Brand not found with id: " + id));
        return mapToBrandResponse(brand);
    }

    @Override
    @Transactional
    public List<BrandResponse> getAllBrands() {
        return brandRepository.findByActiveTrue().stream()
                .map(this::mapToBrandResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Page<BrandResponse> getAllBrands(Pageable pageable) {
        return brandRepository.findByActiveTrue(pageable)
                .map(this::mapToBrandResponse);
    }

    @Override
    public BrandResponse updateBrand(Long id, BrandRequest request) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Brand not found with id: " + id));
        
        if (request.getName() != null && !request.getName().equals(brand.getName())) {
            if (brandRepository.existsByNameAndIdNot(request.getName(), id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Brand with name " + request.getName() + " already exists");
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

    @Override
    public void deleteBrand(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Brand not found with id: " + id));
        
        if (hasProducts(id) > 0) 
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete brand with associated products");
        
        
        brand.setActive(false);
        brandRepository.save(brand);
    }

    @Override
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