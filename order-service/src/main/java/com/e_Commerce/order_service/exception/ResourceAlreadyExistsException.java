package com.e_Commerce.order_service.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when a client attempts to create a resource that already exists.
 */
public class ResourceAlreadyExistsException extends ApiException {
    public ResourceAlreadyExistsException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
