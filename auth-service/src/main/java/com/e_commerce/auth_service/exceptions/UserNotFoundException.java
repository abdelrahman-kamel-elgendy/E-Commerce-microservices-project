package com.e_commerce.auth_service.exceptions;

public class UserNotFoundException extends AuthServiceException {
    public UserNotFoundException(String message) {
        super(message);
    }
}