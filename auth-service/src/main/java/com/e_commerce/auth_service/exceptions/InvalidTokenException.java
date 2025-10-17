package com.e_commerce.auth_service.exceptions;

public class InvalidTokenException extends AuthServiceException {
    public InvalidTokenException(String message) {
        super(message);
    }
}