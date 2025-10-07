package com.e_commerce.auth_service.exceptions;

public class RoleNotFoundException extends AuthServiceException {

    public RoleNotFoundException(String message) {
        super(message);
    }

    public RoleNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
