package com.e_commerce.auth_service.exceptions;

public class UserLockedException extends AuthServiceException {
    public UserLockedException(String message) {
        super(message);
    }
}
