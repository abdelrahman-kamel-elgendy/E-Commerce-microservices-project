package com.e_Commerce.order_service.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class ShippingAddress {

    @Column(name = "shipping_full_name")
    private String fullName;

    @Column(name = "shipping_street")
    private String street;

    @Column(name = "shipping_city")
    private String city;

    @Column(name = "shipping_state")
    private String state;

    @Column(name = "shipping_country")
    private String country;

    @Column(name = "shipping_zip_code")
    private String zipCode;

    @Column(name = "shipping_phone")
    private String phone;
}
