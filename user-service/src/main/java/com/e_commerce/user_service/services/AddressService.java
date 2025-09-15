package com.e_commerce.user_service.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.e_commerce.user_service.dtos.address.AddressDto;
import com.e_commerce.user_service.models.Address;
import com.e_commerce.user_service.models.User;
import com.e_commerce.user_service.repositories.AddressRepository;
import com.e_commerce.user_service.repositories.UserRepository;


@Service
public class AddressService {
    @Autowired
    AddressRepository addressRepository;
    
    @Autowired
    UserRepository userRepository;

    public Address getAddressById(Long id) {
        return addressRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address with id:" + id + " not found"));
    }

    public Address deleteAddress(Long addressId, User user) {
        Address address = this.getAddressById(addressId);
        addressRepository.delete(address);

        user.getAddressIds().remove(address.getId());
        userRepository.save(user);

        return address;
    }

    public Address UpdateAddress(Long id, AddressDto dto) {
        Address address = this.getAddressById(id);

        address.setStreet(dto.getStreet());
        address.setCity(dto.getCity());
        address.setCountry(dto.getCountry());
        address.setPostalCode(dto.getPostalCode());

        return addressRepository.save(address);
    }

    public Address addAddress(AddressDto dto, User user) {
        Address address = new Address();

        address.setStreet(dto.getStreet());
        address.setCity(dto.getCity());
        address.setCountry(dto.getCountry());
        address.setPostalCode(dto.getPostalCode());

        address = addressRepository.save(address);

        user.getAddressIds().add(address.getId());
        userRepository.save(user);

        return address;
    }
}
