package com.e_Commerce.product_service.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Inventory {
    private Long id;
    private Long productId;
    private int quantity;
}
