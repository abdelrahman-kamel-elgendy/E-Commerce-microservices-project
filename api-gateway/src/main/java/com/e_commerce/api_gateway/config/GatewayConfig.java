package com.e_commerce.api_gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
        @Bean
        public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
                return builder.routes()

                                .route("auth-service", r -> r.path("/api/auth/**")
                                                .uri("lb://auth-service"))

                                .route("notification-service", r -> r.path("/api/notification/**")
                                                .uri("lb://notification-service"))

                                .route("user-service", r -> r.path("/api/user/**")
                                                .uri("lb://user-service"))

                                .route("cart-service", r -> r.path("/api/cart/**")
                                                .uri("lb://cart-service"))

                                .route("order-service", r -> r.path("/api/order/**")
                                                .uri("lb://order-service"))

                                .route("product-service", r -> r.path("/api/product/**")
                                                .uri("lb://product-service"))

                                .route("inventory-service", r -> r.path("/api/inventory/**")
                                                .uri("lb://inventory-service"))

                                .build();
        }
}
