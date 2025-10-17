package com.e_commerce.auth_service.exceptions;

public class UserAlreadyExistsException extends AuthServiceException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}