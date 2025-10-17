package com.e_commerce.auth_service.exceptions;

public class TokenExpiredException extends AuthServiceException {
    public TokenExpiredException(String message) {
        super(message);
    }
}
