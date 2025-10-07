package com.e_commerce.auth_service.exceptions;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

        // Handle custom exceptions
        @ExceptionHandler(BadRequestException.class)
        public ResponseEntity<ErrorResponse> handleBadRequestException(
                        BadRequestException ex, WebRequest request) {

                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                "Bad Request!",
                                ex.getMessage(),
                                getRequestPath(request));

                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(TokenExpiredException.class)
        public ResponseEntity<ErrorResponse> handleTokenExpiredException(
                        TokenExpiredException ex, WebRequest request) {

                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.FORBIDDEN.value(),
                                "Token Expired!",
                                ex.getMessage(),
                                getRequestPath(request));

                return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
        }

        @ExceptionHandler(RoleNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleRoleNotFoundException(
                        RoleNotFoundException ex, WebRequest request) {

                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.NOT_FOUND.value(),
                                "Role not found!",
                                ex.getMessage(),
                                getRequestPath(request));

                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(TokenNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleTokenNotFoundException(
                        TokenNotFoundException ex, WebRequest request) {

                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.FORBIDDEN.value(),
                                "Tokn not found!",
                                ex.getMessage(),
                                getRequestPath(request));

                return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
        }

        @ExceptionHandler(UserAlreadyExistsException.class)
        public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(
                        UserAlreadyExistsException ex, WebRequest request) {

                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.CONFLICT.value(),
                                "User Already Exists",
                                ex.getMessage(),
                                getRequestPath(request));

                return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
        }

        @ExceptionHandler(InvalidCredentialsException.class)
        public ResponseEntity<ErrorResponse> handleInvalidCredentialsException(
                        InvalidCredentialsException ex, WebRequest request) {

                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.UNAUTHORIZED.value(),
                                "Invalid Credentials",
                                ex.getMessage(),
                                getRequestPath(request));

                return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }

        @ExceptionHandler(BadCredentialsException.class)
        public ResponseEntity<ErrorResponse> handleBadCredentialsException(
                        BadCredentialsException ex, WebRequest request) {

                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.UNAUTHORIZED.value(),
                                "Authentication Failed",
                                "Invalid email or password",
                                getRequestPath(request));

                return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }

        @ExceptionHandler(UsernameNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(
                        UsernameNotFoundException ex, WebRequest request) {

                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.UNAUTHORIZED.value(),
                                "Authentication Failed",
                                "Invalid email or password",
                                getRequestPath(request));

                return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ErrorResponse> handleAccessDeniedException(
                        AccessDeniedException ex, WebRequest request) {

                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.FORBIDDEN.value(),
                                "Access Denied",
                                "You don't have permission to access this resource",
                                getRequestPath(request));

                return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
        }

        @ExceptionHandler(MalformedJwtException.class)
        public ResponseEntity<ErrorResponse> handleMalformedJwtException(
                        MalformedJwtException ex, WebRequest request) {

                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.UNAUTHORIZED.value(),
                                "Invalid JWT token",
                                ex.getMessage(),
                                getRequestPath(request));

                return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }

        @ExceptionHandler(ExpiredJwtException.class)
        public ResponseEntity<ErrorResponse> handleExpiredJwtException(
                        ExpiredJwtException ex, WebRequest request) {

                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.UNAUTHORIZED.value(),
                                "JWT token is expired",
                                ex.getMessage(),
                                getRequestPath(request));

                return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }

        @ExceptionHandler(UnsupportedJwtException.class)
        public ResponseEntity<ErrorResponse> handleUnsupportedJwtException(
                        UnsupportedJwtException ex, WebRequest request) {

                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.UNAUTHORIZED.value(),
                                "JWT token is unsupported",
                                ex.getMessage(),
                                getRequestPath(request));

                return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
                        IllegalArgumentException ex, WebRequest request) {

                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.UNAUTHORIZED.value(),
                                "JWT claims string is empty",
                                ex.getMessage(),
                                getRequestPath(request));

                return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }

        // Handle validation errors
        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex,
                        WebRequest request) {
                String errorMessage = "Invalid JSON request body";
                if (ex.getMessage() != null && ex.getMessage().contains("end-of-input"))
                        errorMessage = "Request body is empty or invalid JSON";

                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                "BAD_REQUEST",
                                errorMessage,
                                getRequestPath(request));

                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidationExceptions(
                        MethodArgumentNotValidException ex, WebRequest request) {

                List<ValidationError> validationErrors = ex.getBindingResult()
                                .getAllErrors()
                                .stream()
                                .map(error -> {
                                        String fieldName = ((FieldError) error).getField();
                                        String errorMessage = error.getDefaultMessage();
                                        Object rejectedValue = ((FieldError) error).getRejectedValue();
                                        return new ValidationError(fieldName, errorMessage, rejectedValue);
                                })
                                .collect(Collectors.toList());

                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.BAD_REQUEST.value(),
                                "Validation Failed",
                                "Request validation failed",
                                getRequestPath(request),
                                validationErrors);

                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        // Handle generic runtime exceptions
        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<ErrorResponse> handleRuntimeException(
                        RuntimeException ex, WebRequest request) {

                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                ex.getClass().toString(),
                                ex.getMessage(),
                                getRequestPath(request));

                return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Handle all other exceptions
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGenericException(
                        Exception ex, WebRequest request) {

                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                ex.getClass().toString(),
                                ex.getMessage(),
                                getRequestPath(request));

                return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Helper method to get request path
        private String getRequestPath(WebRequest request) {
                if (request instanceof ServletWebRequest) {
                        HttpServletRequest servletRequest = ((ServletWebRequest) request).getRequest();
                        return servletRequest.getRequestURI();
                }
                return "Unknown";
        }
}
