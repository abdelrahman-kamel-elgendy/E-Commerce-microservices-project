package com.e_Commerce.product_service.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.e_Commerce.product_service.models.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByActive(boolean active);
}
