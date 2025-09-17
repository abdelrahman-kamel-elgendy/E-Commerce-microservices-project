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
                .route("notification-service", r -> r.path("/api/notifications/**", "/api/emails/**")
                        .uri("lb://notification-service"))


                .route("user-service-auth", r -> r.path("/api/auth/**")
                        .uri("lb://user-service"))

                .route("user-service-auth", r -> r.path("/api/profile/**")
                        .uri("lb://user-service"))

                .route("user-service-users", r -> r.path("/api/users/**")
                        .uri("lb://user-service"))

                        
                .route("product-service-products", r -> r.path("/api/products/**")
                        .uri("lb://product-service"))
                
                        .route("product-service-categories", r -> r.path("/api/categories/**")
                        .uri("lb://product-service"))


                .route("inventory-service", r -> r.path("/api/inventories/**")
                        .uri("lb://inventory-service"))


                .route("cart-service", r -> r.path("/api/carts/**")
                        .uri("lb://cart-service"))
                        
                .build();
    }
}