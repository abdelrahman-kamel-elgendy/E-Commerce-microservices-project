package com.e_commerce.api_gateway.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Component
@Order(-1)
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final ObjectMapper objectMapper;

    public GlobalExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    @NonNull
    public Mono<Void> handle(@NonNull ServerWebExchange exchange, @NonNull Throwable ex) {
        HttpStatus status = determineHttpStatus(ex);
        String path = exchange.getRequest().getPath().value();

        ErrorResponse errorResponse = createErrorResponse(status, ex, path);

        return writeErrorResponseDirectly(exchange, status, errorResponse);
    }

    private HttpStatus determineHttpStatus(Throwable ex) {
        if (ex instanceof JwtAuthenticationException) {
            return HttpStatus.UNAUTHORIZED;
        } else if (ex instanceof NotFoundException ||
                ex instanceof org.springframework.cloud.gateway.support.NotFoundException ||
                ex instanceof java.net.ConnectException ||
                ex instanceof ServiceUnavailableException) {
            return HttpStatus.SERVICE_UNAVAILABLE;
        } else if (ex instanceof org.springframework.cloud.gateway.support.TimeoutException) {
            return HttpStatus.GATEWAY_TIMEOUT;
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    private ErrorResponse createErrorResponse(HttpStatus status, Throwable ex, String path) {
        String errorMessage = getErrorMessage(ex);
        String service = getServiceName(ex);

        return new ErrorResponse(
                status,
                status.getReasonPhrase(),
                errorMessage,
                path,
                service);
    }

    private String getErrorMessage(Throwable ex) {
        if (ex instanceof JwtAuthenticationException) {
            return ex.getMessage();
        } else if (ex instanceof ServiceUnavailableException) {
            return ex.getMessage();
        } else if (ex instanceof NotFoundException ||
                ex instanceof org.springframework.cloud.gateway.support.NotFoundException) {
            return "Service is temporarily unavailable. Please try again later.";
        } else if (ex instanceof java.net.ConnectException) {
            return "Unable to connect to backend service. Please try again later.";
        } else if (ex instanceof org.springframework.cloud.gateway.support.TimeoutException) {
            return "Request timeout. The service is taking too long to respond.";
        } else {
            return "An unexpected error occurred in the API Gateway";
        }
    }

    private String getServiceName(Throwable ex) {
        if (ex instanceof NotFoundException) {
            String message = ex.getMessage();
            if (message != null && message.contains("Unable to find instance for")) {
                return extractServiceName(message);
            }
        }
        return null;
    }

    private String extractServiceName(String message) {
        try {
            if (message.contains("for")) {
                return message.substring(message.indexOf("for") + 4).trim();
            }
        } catch (Exception e) {
            logger.debug("Could not extract service name from message: {}", message);
        }
        return "unknown";
    }

    @NonNull
    private Mono<Void> writeErrorResponseDirectly(@NonNull ServerWebExchange exchange,
            HttpStatus status,
            ErrorResponse errorResponse) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        exchange.getResponse().getHeaders().set("X-Gateway-Error", "true");

        try {
            String jsonResponse = objectMapper.writeValueAsString(errorResponse);
            byte[] bytes = jsonResponse.getBytes(StandardCharsets.UTF_8);

            DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();
            DataBuffer buffer = bufferFactory.wrap(bytes);

            return exchange.getResponse().writeWith(Mono.just(buffer));

        } catch (JsonProcessingException e) {
            logger.error("Error serializing error response: {}", e.getMessage());

            return createFallbackResponse(exchange, status);
        }
    }

    @NonNull
    private Mono<Void> createFallbackResponse(@NonNull ServerWebExchange exchange, HttpStatus status) {
        String fallbackJson = String.format(
                "{\"timestamp\":\"%s\",\"status\":%d,\"error\":\"%s\",\"message\":\"%s\"}",
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                "An error occurred while processing your request");

        byte[] bytes = fallbackJson.getBytes(StandardCharsets.UTF_8);
        DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();
        DataBuffer buffer = bufferFactory.wrap(bytes);

        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}