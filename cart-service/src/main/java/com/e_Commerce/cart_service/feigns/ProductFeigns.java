package com.e_Commerce.cart_service.feigns;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.e_Commerce.cart_service.dtos.ProductDetails;
import com.e_Commerce.cart_service.res.ApiResponse;

@FeignClient(name = "product-service")
public interface ProductFeigns {
    
    @GetMapping("/api/product")
    public ResponseEntity<ApiResponse<ProductDetails>> getProductById(
        @RequestParam Long id
    );
}
