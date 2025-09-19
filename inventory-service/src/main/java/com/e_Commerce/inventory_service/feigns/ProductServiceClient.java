package com.e_Commerce.inventory_service.feigns;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.e_Commerce.inventory_service.dto.ProductResponse;

@FeignClient(name = "product-service")
public interface ProductServiceClient {
    @GetMapping("/api/products/id")
    public ResponseEntity<ProductResponse> getProductById(@RequestParam Long id);

    @GetMapping("/api/products/check")
    public ResponseEntity<Boolean> checkProductExistence(
        @RequestParam Long id,
        @RequestParam String sku
    );
}
