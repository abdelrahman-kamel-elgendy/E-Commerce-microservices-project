package com.e_commerce.user_service.dto.response;

import com.e_commerce.user_service.models.Address;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AddressResponse {
    private Long id;
    private String street;
    private String city;
    private String state;
    private String country;

    private String zipCode;

    public AddressResponse(Address address) {
        this.id = address.getId();
        this.street = address.getStreet();
        this.city = address.getCity();
        this.state = address.getState();
        this.country = address.getCountry();
        this.zipCode = address.getZipCode();
    }
}
