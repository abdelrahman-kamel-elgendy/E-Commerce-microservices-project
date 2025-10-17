package com.e_commerce.auth_service.exceptions;

public class AuthServiceException extends RuntimeException {
    public AuthServiceException(String message) {
        super(message);
    }

    public AuthServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
