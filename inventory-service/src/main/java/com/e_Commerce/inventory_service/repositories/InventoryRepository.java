package com.e_Commerce.inventory_service.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.e_Commerce.inventory_service.models.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByName(String name);

    List<Inventory> findByActiveTrue();

    Page<Inventory> findByActiveTrue(Pageable pageable);
}
