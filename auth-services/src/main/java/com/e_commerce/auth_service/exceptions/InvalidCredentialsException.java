package com.e_commerce.auth_service.exceptions;

public class InvalidCredentialsException extends AuthServiceException {
    public InvalidCredentialsException(String message) {
        super(message);
    }

    public InvalidCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}