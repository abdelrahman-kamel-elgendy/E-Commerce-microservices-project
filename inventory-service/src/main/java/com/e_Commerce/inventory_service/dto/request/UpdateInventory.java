package com.e_Commerce.inventory_service.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateInventory {
    private String name;
    private String code;
    private String address;
    private String city;
    private String postalCode;
    private String country;
}
