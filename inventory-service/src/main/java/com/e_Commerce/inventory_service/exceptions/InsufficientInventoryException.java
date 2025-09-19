package com.e_Commerce.inventory_service.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Thrown when there isn't enough inventory to satisfy an operation.
 */
public class InsufficientInventoryException extends ApiException {
    public InsufficientInventoryException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
