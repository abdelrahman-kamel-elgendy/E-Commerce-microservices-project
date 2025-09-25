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
                
                // notification service
                .route("notification-service", r -> r.path("/api/notifications/**", "/api/emails/**")
                        .uri("lb://notification-service"))

                // user service
                .route("user-service-auth", r -> r.path("/api/auth/**")
                        .uri("lb://user-service"))
                .route("user-service-auth", r -> r.path("/api/profile/**")
                        .uri("lb://user-service"))
                .route("user-service-users", r -> r.path("/api/users/**")
                        .uri("lb://user-service"))

                // product service        
                .route("product-service", r -> r.path("/api/products/**")
                        .uri("lb://product-service"))
                .route("product-service", r -> r.path("/api/categories/**")
                        .uri("lb://product-service"))
                .route("product-service", r -> r.path("/api/brands/**")
                        .uri("lb://product-service"))

                // inventory service
                .route("inventory-service", r -> r.path("/api/inventories/**")
                        .uri("lb://inventory-service"))

                // cart service
                .route("cart-service", r -> r.path("/api/carts/**")
                        .uri("lb://cart-service"))
                        
                // order service 
                .route("order-service", r -> r.path("/api/orders/**")
                        .uri("lb://order-service"))
                        
                .build();
    }
}