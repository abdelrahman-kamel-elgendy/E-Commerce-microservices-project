package com.e_commerce.api_gateway.config;

import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Configuration
public class GatewayErrorHandlerConfig {

    @Bean
    @Order(-1)
    public ErrorWebExceptionHandler globalErrorHandler() {
        return (ServerWebExchange exchange, Throwable ex) -> {
            ServerHttpResponse response = exchange.getResponse();
            response.getHeaders().add("Content-Type", "application/json");

            // 🔹 Case 1: Downstream service threw an HTTP error (4xx / 5xx)
            if (ex instanceof WebClientResponseException webEx) {
                byte[] body = webEx.getResponseBodyAsByteArray();
                response.setStatusCode(webEx.getStatusCode());
                return response.writeWith(Mono.just(response.bufferFactory().wrap(body)));
            }

            // 🔹 Case 2: Try to map other known exception types to a proper HTTP status
            HttpStatus mappedStatus = null;

            // If the exception is a ResponseStatusException (common in Spring) use its
            // status
            if (ex instanceof ResponseStatusException rse) {
                HttpStatus resolved = HttpStatus.resolve(rse.getStatusCode().value());
                if (resolved != null)
                    mappedStatus = resolved;
            }

            // If the cause is a WebClientResponseException or ResponseStatusException,
            // prefer that
            else if (ex.getCause() instanceof WebClientResponseException causeWebEx) {
                HttpStatus resolved = HttpStatus.resolve(causeWebEx.getStatusCode().value());
                if (resolved != null)
                    mappedStatus = resolved;
            } else if (ex.getCause() instanceof ResponseStatusException causeRse) {
                HttpStatus resolved = HttpStatus.resolve(causeRse.getStatusCode().value());
                if (resolved != null)
                    mappedStatus = resolved;
            }

            // Fallback: try to parse an HTTP status code from the exception message
            // (e.g."503 SERVICE_UNAVAILABLE ...")
            if (mappedStatus == null && ex.getMessage() != null) {
                java.util.regex.Pattern p = java.util.regex.Pattern.compile("(\\d{3})");
                java.util.regex.Matcher m = p.matcher(ex.getMessage());
                if (m.find()) {
                    try {
                        int code = Integer.parseInt(m.group(1));
                        HttpStatus resolved = HttpStatus.resolve(code);
                        if (resolved != null) {
                            mappedStatus = resolved;
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }

            HttpStatus finalStatus = mappedStatus != null ? mappedStatus : HttpStatus.INTERNAL_SERVER_ERROR;
            String rawMessage = ex.getMessage();
            String cleanedMessage = cleanMessage(rawMessage);

            String fallbackJson = String.format("""
                    {
                        "timestamp": "%s",
                        "status": %d,
                        "error": "%s",
                        "message": "%s",
                        "path": "%s"
                    }
                    """,
                    java.time.LocalDateTime.now(),
                    finalStatus.value(),
                    finalStatus.getReasonPhrase(),
                    cleanedMessage,
                    exchange.getRequest().getPath());

            byte[] bytes = fallbackJson.getBytes(StandardCharsets.UTF_8);
            response.setStatusCode(finalStatus);
            return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
        };
    }

    /**
     * Remove a leading status prefix like "404 NOT_FOUND " from exception messages
     * and trim surrounding quotes. Returns the original string if nothing to strip.
     */
    private static String cleanMessage(String raw) {
        if (raw == null)
            return null;
        String s = raw.trim();

        // Pattern: optional leading digits and status text, e.g. "404 NOT_FOUND "
        java.util.regex.Pattern prefix = java.util.regex.Pattern.compile("^\\s*\\d{3}\\s+[A-Z_]+\\s*");
        java.util.regex.Matcher m = prefix.matcher(s);
        if (m.find()) {
            s = s.substring(m.end()).trim();
        }

        // If message is wrapped in quotes, remove them
        if ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'"))) {
            s = s.substring(1, s.length() - 1).trim();
        }

        return s;
    }
}
