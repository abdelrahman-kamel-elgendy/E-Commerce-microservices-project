package com.e_commerce.auth_service.exceptions;

public class TokenNotFoundException extends AuthServiceException {
    public TokenNotFoundException(String message) {
        super(message);
    }

    public TokenNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}