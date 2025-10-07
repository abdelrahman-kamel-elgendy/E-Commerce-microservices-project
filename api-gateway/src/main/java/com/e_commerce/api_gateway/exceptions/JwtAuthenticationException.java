package com.e_commerce.api_gateway.exceptions;

public class JwtAuthenticationException extends GatewayException {
    public JwtAuthenticationException(String message) {
        super(message);
    }

    public JwtAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
