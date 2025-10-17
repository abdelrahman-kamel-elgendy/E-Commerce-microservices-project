package com.e_commerce.auth_service.exceptions;

public class AccessDeniedException extends AuthServiceException {
    public AccessDeniedException(String message) {
        super(message);
    }
}