package com.e_commerce.api_gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

        @Bean
        public RouteLocator routes(RouteLocatorBuilder builder) {
                return builder.routes()
                                .route("auth-service", r -> r.path("/api/auth/**")
                                                .uri("lb://auth-service"))

                                .route("user-service", r -> r.path("/api/users/**")
                                                .uri("lb://user-service"))

                                .route("product-service", r -> r.path("/api/products/**")
                                                .uri("lb://product-service"))

                                .route("order-service", r -> r.path("/api/orders/**")
                                                .uri("lb://order-service"))

                                .route("payment-service", r -> r.path("/api/payments/**")
                                                .uri("lb://payment-service"))

                                .route("inventory-service", r -> r.path("/api/inventory/**")
                                                .uri("lb://inventory-service"))

                                .route("notification-service", r -> r.path("/api/notifications/**")
                                                .uri("lb://notification-service"))

                                .build();
        }
}