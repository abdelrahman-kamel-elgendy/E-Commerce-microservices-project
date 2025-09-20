package com.e_Commerce.cart_service.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when a client attempts to create a resource that already exists.
 */
public class ResourceAlreadyExistsException extends ApiException {
    public ResourceAlreadyExistsException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
