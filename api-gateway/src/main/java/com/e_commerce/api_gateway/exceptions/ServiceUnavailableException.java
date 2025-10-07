package com.e_commerce.api_gateway.exceptions;

public class ServiceUnavailableException extends GatewayException {
    public ServiceUnavailableException(String message) {
        super(message);
    }

    public ServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
