package com.e_Commerce.cart_service.exception;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.dao.DataIntegrityViolationException;

import jakarta.validation.ConstraintViolationException;
import feign.FeignException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex, HttpServletRequest request) {
        HttpStatus status = ex.getStatus() != null ? ex.getStatus() : HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponse body = new ErrorResponse(
            false,
            ex.getMessage(),
            LocalDateTime.now(),
            status.value(),
            status.getReasonPhrase(),
            request.getRequestURI(),
            null,
            null
        );
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(ResponseStatusException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException ex, HttpServletRequest request) {
        HttpStatus status = (HttpStatus) ex.getStatusCode();
        ErrorResponse body = new ErrorResponse(
            false,
            ex.getReason() == null ? ex.getMessage() : ex.getReason(),
            LocalDateTime.now(),
            status.value(),
            status.getReasonPhrase(),
            request.getRequestURI(),
            null,
            null
        );
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleAlreadyExists(ResourceAlreadyExistsException ex, HttpServletRequest request) {
        HttpStatus status = ex.getStatus();
        ErrorResponse body = new ErrorResponse(
            false,
            ex.getMessage(),
            LocalDateTime.now(),
            status.value(),
            status.getReasonPhrase(),
            request.getRequestURI(),
            "RESOURCE_ALREADY_EXISTS",
            null
        );
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        ErrorResponse body = new ErrorResponse(
            false,
            "Database constraint violation: " + ex.getMostSpecificCause().getMessage(),
            LocalDateTime.now(),
            status.value(),
            status.getReasonPhrase(),
            request.getRequestURI(),
            "DATA_INTEGRITY_VIOLATION",
            null
        );
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse body = new ErrorResponse(
            false,
            "Validation error: " + ex.getMessage(),
            LocalDateTime.now(),
            status.value(),
            status.getReasonPhrase(),
            request.getRequestURI(),
            "VALIDATION_ERROR",
            ex.getConstraintViolations().stream()
                .map(cv -> Map.of("property", cv.getPropertyPath().toString(), "message", cv.getMessage()))
                .collect(Collectors.toList())
        );
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(FeignException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleFeignException(FeignException ex, HttpServletRequest request) {
        int statusCode = ex.status();
        HttpStatus status = HttpStatus.resolve(statusCode);
        if (status == null) status = HttpStatus.BAD_GATEWAY;

        String message = "Downstream service error";
        if (status == HttpStatus.SERVICE_UNAVAILABLE) {
            message = "Downstream service unavailable: " + ex.getMessage();
        } else {
            message = "Downstream service returned status " + status.value() + ": " + ex.getMessage();
        }

        ErrorResponse body = new ErrorResponse(
            false,
            message,
            LocalDateTime.now(),
            status.value(),
            status.getReasonPhrase(),
            request.getRequestURI(),
            "FEIGN_ERROR",
            null
        );
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(java.util.NoSuchElementException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleNoSuchElement(java.util.NoSuchElementException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponse body = new ErrorResponse(
            false,
            ex.getMessage() == null ? "Resource not found" : ex.getMessage(),
            LocalDateTime.now(),
            status.value(),
            status.getReasonPhrase(),
            request.getRequestURI(),
            "NOT_FOUND",
            ex.getClass()
        );
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<Map<String, String>> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> Map.of("field", fe.getField(), "message", fe.getDefaultMessage()))
                .collect(Collectors.toList());

        String message = "Method Argument Not Valid Exception";

        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse body = new ErrorResponse(
            false,
            message,
            LocalDateTime.now(),
            status.value(),
            status.getReasonPhrase(),
            request.getRequestURI(),
            "VALIDATION_ERROR",
            fieldErrors
        );
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponse body = new ErrorResponse(
            false,
            "Internal server error",
            LocalDateTime.now(),
            status.value(),
            status.getReasonPhrase(),
            request.getRequestURI(),
            null,
            ex.getClass()
        );
        return new ResponseEntity<>(body, status);
    }
}
