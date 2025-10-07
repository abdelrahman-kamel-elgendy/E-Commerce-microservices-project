package com.e_commerce.api_gateway.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.e_commerce.api_gateway.exceptions.JwtAuthenticationException;
import com.e_commerce.api_gateway.util.JwtUtil;

import reactor.core.publisher.Mono;

@Component
public class GlobalJwtAuthenticationFilter implements GlobalFilter, Ordered {

    @Autowired
    JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        String method = request.getMethod().name();

        // Skip authentication for public endpoints
        if (isPublicEndpoint(path, method)) {
            return chain.filter(exchange);
        }

        // Check for Authorization header
        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION))
            throw new JwtAuthenticationException("Missing authorization header");

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            throw new JwtAuthenticationException("Invalid authorization header format");

        String token = authHeader.substring(7);
        try {
            // Validate token
            if (!jwtUtil.validateToken(token))
                throw new JwtAuthenticationException("Invalid or expired JWT token");

            // Extract user information
            String username = jwtUtil.getUsernameFromToken(token);
            String roles = jwtUtil.getRolesFromToken(token);
            Long userId = jwtUtil.getUserIdFromToken(token);

            // Add headers to the request
            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                    .header("X-User-Id", userId != null ? userId.toString() : "")
                    .header("X-User-Email", username != null ? username : "")
                    .header("X-User-Roles", roles != null ? roles : "")
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build());

        } catch (Exception e) {
            throw new JwtAuthenticationException("Authentication failed: " + e.getMessage());
        }
    }

    private boolean isPublicEndpoint(String path, String method) {
        // Auth endpoints (all methods)
        if (path.startsWith("/auth/") || path.equals("/auth")) {
            return true;
        }

        // Health checks
        if (path.startsWith("/actuator/health") || path.equals("/actuator/health")) {
            return true;
        }

        // Eureka endpoints
        if (path.startsWith("/eureka/") || path.equals("/")) {
            return true;
        }

        // Swagger/OpenAPI documentation (if you have it)
        if (path.contains("/v3/api-docs") || path.contains("/swagger") || path.contains("/webjars")) {
            return true;
        }

        return false;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}