package com.e_Commerce.product_service.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.e_Commerce.product_service.dtos.request.BrandRequest;
import com.e_Commerce.product_service.dtos.response.ApiResponse;
import com.e_Commerce.product_service.dtos.response.BrandResponse;
import com.e_Commerce.product_service.services.BrandService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/brands")
public class BrandController {

    @Autowired
    BrandService brandService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<BrandResponse>> createBrand(@Valid @RequestBody BrandRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            new ApiResponse<BrandResponse>(
                true,
                "Brand created successflly",
                brandService.createBrand(request)
            )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BrandResponse>> getBrandById(@PathVariable Long id) {
        return ResponseEntity.ok(
            new ApiResponse<BrandResponse>(
                true,
                "Brand retrieved successflly",
                brandService.getBrandById(id)
            )
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BrandResponse>>> getAllBrands() {
        return ResponseEntity.ok(
            new ApiResponse<List<BrandResponse>>(
                true,
                "Brands retrieved successflly",
                brandService.getAllBrands()
            )
        );
    }

    @GetMapping("/page")
    public ResponseEntity<ApiResponse<Page<BrandResponse>>> getAllBrands(Pageable pageable) {
        return ResponseEntity.ok(
            new ApiResponse<Page<BrandResponse>>(
                true,
                "Brands retrieved successflly",
                brandService.getAllBrands(pageable)
            )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BrandResponse>> updateBrand(
        @PathVariable Long id, 
        @Valid @RequestBody BrandRequest request
    ) {
        return ResponseEntity.ok(
            new ApiResponse<BrandResponse>(
                true,
                "Brand updated successflly",
                brandService.updateBrand(id, request)
            )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBrand(@PathVariable Long id) {
        brandService.deleteBrand(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/has-products")
    public ResponseEntity<ApiResponse<Long>> hasProducts(@PathVariable Long id) {
        long hasProducts = brandService.hasProducts(id);
        return ResponseEntity.ok(
            new ApiResponse<Long>(
                true,
                hasProducts > 0?"Brand has products":"Brand has no products",
                hasProducts
            )
        );
    }
}