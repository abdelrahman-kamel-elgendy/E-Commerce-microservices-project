package com.e_Commerce.product_service.services;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.e_Commerce.product_service.dtos.ProductDto;
import com.e_Commerce.product_service.models.Product;
import com.e_Commerce.product_service.repositories.ProductRepository;

@Service
public class ProductService {
    @Autowired
    ProductRepository productRepository;

    public Product createProduct(ProductDto dto) {
        return productRepository.save(new Product(dto));
    }

    public Product updateProduct(Long id, ProductDto dto) {
        Product product = this.getProductById(id);

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setCategoryId(dto.getCategoryId());

        product.setUpdatedAt(Instant.now());

        return productRepository.save(new Product(dto));
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getAllActiveProducts() {
        return productRepository.findAllByActive(true);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product with id " + id + " not found"));
    }

    public Product deleteProduct(Long id) {
        Product product = this.getProductById(id);
        productRepository.delete(product);
        return product;
    }

    public Product ProductActivation(Long id, boolean active) {
        Product product = this.getProductById(id);
        product.setActive(active);
        product.setUpdatedAt(Instant.now());
        return productRepository.save(product);
    }
}
