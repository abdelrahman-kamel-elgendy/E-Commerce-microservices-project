package com.e_Commerce.cart_service.exception;

import org.springframework.http.HttpStatus;

/**
 * Indicates the downstream Feign client service is unavailable.
 */
public class FeignServiceUnavailableException extends ApiException {
    public FeignServiceUnavailableException(String message) {
        super(HttpStatus.SERVICE_UNAVAILABLE, message);
    }
}
