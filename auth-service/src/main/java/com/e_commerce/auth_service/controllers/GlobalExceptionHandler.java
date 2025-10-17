package com.e_commerce.auth_service.controllers;

import com.e_commerce.auth_service.dto.response.ErrorResponse;
import com.e_commerce.auth_service.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

        private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

        // Handle custom authentication exceptions
        @ExceptionHandler(InvalidCredentialsException.class)
        public ResponseEntity<ErrorResponse> handleInvalidCredentialsException(
                        InvalidCredentialsException ex, WebRequest request) {

                logger.warn("Invalid credentials attempt: {}", ex.getMessage());

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .status(HttpStatus.UNAUTHORIZED.value())
                                .error("Invalid Credentials")
                                .message(ex.getMessage())
                                .path(getRequestPath(request))
                                .build();

                return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }

        @ExceptionHandler(UserNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleUserNotFoundException(
                        UserNotFoundException ex, WebRequest request) {

                logger.warn("User not found: {}", ex.getMessage());

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .status(HttpStatus.NOT_FOUND.value())
                                .error("User Not Found")
                                .message(ex.getMessage())
                                .path(getRequestPath(request))
                                .build();

                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(UserAlreadyExistsException.class)
        public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(
                        UserAlreadyExistsException ex, WebRequest request) {

                logger.warn("User already exists: {}", ex.getMessage());

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .status(HttpStatus.CONFLICT.value())
                                .error("User Already Exists")
                                .message(ex.getMessage())
                                .path(getRequestPath(request))
                                .build();

                return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
        }

        @ExceptionHandler(InvalidTokenException.class)
        public ResponseEntity<ErrorResponse> handleInvalidTokenException(
                        InvalidTokenException ex, WebRequest request) {

                logger.warn("Invalid token: {}", ex.getMessage());

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .status(HttpStatus.UNAUTHORIZED.value())
                                .error("Invalid Token")
                                .message(ex.getMessage())
                                .path(getRequestPath(request))
                                .build();

                return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }

        @ExceptionHandler(TokenExpiredException.class)
        public ResponseEntity<ErrorResponse> handleTokenExpiredException(
                        TokenExpiredException ex, WebRequest request) {

                logger.warn("Token expired: {}", ex.getMessage());

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .status(HttpStatus.UNAUTHORIZED.value())
                                .error("Token Expired")
                                .message(ex.getMessage())
                                .path(getRequestPath(request))
                                .build();

                return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }

        @ExceptionHandler(com.e_commerce.auth_service.exceptions.UserLockedException.class)
        public ResponseEntity<ErrorResponse> handleUserLockedException(
                        com.e_commerce.auth_service.exceptions.UserLockedException ex, WebRequest request) {

                logger.warn("User account locked: {}", ex.getMessage());

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .status(HttpStatus.LOCKED.value())
                                .error("Account Locked")
                                .message(ex.getMessage())
                                .path(getRequestPath(request))
                                .build();

                return new ResponseEntity<>(errorResponse, HttpStatus.LOCKED);
        }

        @ExceptionHandler(ValidationException.class)
        public ResponseEntity<ErrorResponse> handleValidationException(
                        ValidationException ex, WebRequest request) {

                logger.warn("Validation error: {}", ex.getMessage());

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error("Validation Failed")
                                .message(ex.getMessage())
                                .path(getRequestPath(request))
                                .build();

                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        // Handle Spring Security exceptions
        @ExceptionHandler(AuthenticationException.class)
        public ResponseEntity<ErrorResponse> handleAuthenticationException(
                        AuthenticationException ex, WebRequest request) {

                logger.warn("Bad credentials: {}", ex.getMessage());

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .status(HttpStatus.UNAUTHORIZED.value())
                                .error("Authentication Failed")
                                .message("Invalid username or password")
                                .path(getRequestPath(request))
                                .build();

                return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }

        @ExceptionHandler(BadCredentialsException.class)
        public ResponseEntity<ErrorResponse> handleBadCredentialsException(
                        BadCredentialsException ex, WebRequest request) {

                logger.warn("Bad credentials: {}", ex.getMessage());

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .status(HttpStatus.UNAUTHORIZED.value())
                                .error("Authentication Failed")
                                .message("Invalid username or password")
                                .path(getRequestPath(request))
                                .build();

                return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ErrorResponse> handleAccessDeniedException(
                        AccessDeniedException ex, WebRequest request) {

                logger.warn("Access denied: {}", ex.getMessage());

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .status(HttpStatus.FORBIDDEN.value())
                                .error("Access Denied")
                                .message("You don't have permission to access this resource")
                                .path(getRequestPath(request))
                                .build();

                return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
        }

        @ExceptionHandler(UsernameNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(
                        UsernameNotFoundException ex, WebRequest request) {

                logger.warn("Username not found: {}", ex.getMessage());

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .status(HttpStatus.NOT_FOUND.value())
                                .error("User Not Found")
                                .message(ex.getMessage())
                                .path(getRequestPath(request))
                                .build();

                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        // Handle validation errors from @Valid
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidationExceptions(
                        MethodArgumentNotValidException ex, WebRequest request) {

                List<Map<String, String>> errors = ex.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .map(error -> {
                                        Map<String, String> err = new HashMap<>();
                                        err.put("field", error.getField());
                                        err.put("message", error.getDefaultMessage());
                                        return err;
                                })
                                .toList();

                logger.warn("Validation errors: {}", errors);

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .error("Validation Failed")
                                .message("Request validation failed")
                                .path(getRequestPath(request))
                                .detail(errors)
                                .build();

                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        // Handle generic runtime exceptions
        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<ErrorResponse> handleRuntimeException(
                        RuntimeException ex, WebRequest request) {

                logger.error("Runtime exception occurred: {}", ex.getMessage(), ex);

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .error("Internal Server Error")
                                .message("An unexpected error occurred")
                                .path(getRequestPath(request))
                                .build();

                return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Handle all other exceptions
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGenericException(
                        Exception ex, WebRequest request) {

                logger.error("Unhandled exception occurred: {}", ex.getMessage(), ex);

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .error("Internal Server Error")
                                .message("An unexpected error occurred")
                                .path(getRequestPath(request))
                                .detail(ex.getClass().getName() + ": " + ex.getMessage())
                                .build();

                return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Catch Throwable (Errors like NoClassDefFoundError) and return a clean JSON
        // response
        @ExceptionHandler(Throwable.class)
        public ResponseEntity<ErrorResponse> handleThrowable(Throwable t, WebRequest request) {
                logger.error("Unhandled throwable occurred: {}", t.getMessage(), t);

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .error("Internal Server Error")
                                .message("An unexpected error occurred")
                                .path(getRequestPath(request))
                                .detail(t.getClass().getName() + ": " + t.getMessage())
                                .build();

                return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Helper method to extract request path
        private String getRequestPath(WebRequest request) {
                if (request instanceof ServletWebRequest) {
                        ServletWebRequest servletWebRequest = (ServletWebRequest) request;
                        return servletWebRequest.getRequest().getRequestURI();
                }
                return "Unknown";
        }
}