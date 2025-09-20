package com.e_Commerce.cart_service.exception;
import org.springframework.http.HttpStatus;

/**
 * Thrown when a client provides an invalid request (bad input).
 */
public class InvalidRequestException extends ApiException {
    public InvalidRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
