package com.e_commerce.user_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.e_commerce.user_service.models.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    
}
