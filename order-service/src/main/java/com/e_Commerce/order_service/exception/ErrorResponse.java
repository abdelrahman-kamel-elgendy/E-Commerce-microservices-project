package com.e_Commerce.order_service.exception;

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
    private Integer status;
    private String error;
    private String path;
    private String errorCode;
    private Object details;
}
