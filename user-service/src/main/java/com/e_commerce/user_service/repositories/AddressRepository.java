package com.e_commerce.user_service.repositories;

import com.e_commerce.user_service.models.Address;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    Page<Address> findByUserIdAndActiveTrue(Long userId, Pageable pageable);
}
