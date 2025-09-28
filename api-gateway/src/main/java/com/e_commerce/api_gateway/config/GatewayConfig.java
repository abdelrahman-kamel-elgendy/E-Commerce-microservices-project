package com.e_commerce.api_gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

        @Bean
        public RouteLocator routes(RouteLocatorBuilder builder, JwtAuthenticationFilter filter) {
                return builder.routes()
                                .route("auth-service", r -> r.path("/auth/**")
                                                .uri("lb://auth-service"))
                                .route("user-service", r -> r.path("/users/**")
                                                .filters(f -> f.filter(filter))
                                                .uri("lb://user-service"))
                                .route("product-service", r -> r.path("/products/**")
                                                .filters(f -> f.filter(filter))
                                                .uri("lb://product-service"))
                                .route("order-service", r -> r.path("/orders/**")
                                                .filters(f -> f.filter(filter))
                                                .uri("lb://order-service"))
                                .route("payment-service", r -> r.path("/payments/**")
                                                .filters(f -> f.filter(filter))
                                                .uri("lb://payment-service"))
                                .build();
        }
}