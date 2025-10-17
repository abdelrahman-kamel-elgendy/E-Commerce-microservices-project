package com.e_commerce.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;

import java.nio.charset.StandardCharsets;

@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {
    @Value("${gateway.auth.enabled:false}")
    private boolean authEnabled;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!authEnabled)
            return chain.filter(exchange);

        var request = exchange.getRequest();
        String path = request.getPath().value();

        // Skip health and actuator endpoints
        if (path.startsWith("/actuator") || path.startsWith("/actuator/")) {
            return chain.filter(exchange);
        }

        var authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader == null || authHeader.isBlank()) {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            byte[] bytes = "{\"error\":\"Unauthorized\"}".getBytes(StandardCharsets.UTF_8);
            return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
        }

        // In a real project validate JWT or forward to auth-service
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // run before many other filters
        return Ordered.HIGHEST_PRECEDENCE + 50;
    }
}
