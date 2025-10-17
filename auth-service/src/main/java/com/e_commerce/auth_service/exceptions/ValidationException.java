package com.e_commerce.auth_service.exceptions;

public class ValidationException extends AuthServiceException {
    public ValidationException(String message) {
        super(message);
    }
}