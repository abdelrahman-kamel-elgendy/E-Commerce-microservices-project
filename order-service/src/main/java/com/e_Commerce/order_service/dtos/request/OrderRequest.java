package com.e_Commerce.order_service.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderRequest {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Shipping address is required")
    @Size(max = 500, message = "Shipping address must be less than 500 characters")
    private String shippingAddress;

    @NotBlank(message = "Customer email is required")
    @Email(message = "Email should be valid")
    private String customerEmail;

    @NotBlank(message = "Customer phone is required")
    private String customerPhone;
}