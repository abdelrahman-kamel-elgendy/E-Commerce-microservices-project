package com.e_Commerce.inventory_service.exceptions;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private boolean success;
    private String message;
    private LocalDateTime timestamp;
    private int status;           // HTTP status code
    private String error;         // Reason phrase (HttpStatus.name())
    private String path;          // Request path
    private String errorCode;     // Optional application-specific error code
    private Object details;       // Optional details (e.g., validation errors)
}
