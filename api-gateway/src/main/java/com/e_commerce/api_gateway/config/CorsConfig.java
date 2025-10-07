package com.e_commerce.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Configuration
public class CorsConfig {

    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    @Value("${cors.allowed-methods")
    private String allowedMethods;

    @Value("${cors.allowed-headers}")
    private String allowedHeaders;

    @Value("${cors.exposed-headers}")
    private String exposedHeaders;

    @Value("${cors.max-age}")
    private Long maxAge;

    @Bean
    public CorsWebFilter corsWebFilter() {
        List<String> originsList = parseCommaSeparatedString(allowedOrigins);
        List<String> methodsList = parseCommaSeparatedString(allowedMethods);
        List<String> headersList = parseCommaSeparatedString(allowedHeaders);
        List<String> exposedHeadersList = parseCommaSeparatedString(exposedHeaders);

        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(originsList);
        corsConfig.setMaxAge(maxAge);
        corsConfig.setAllowedMethods(methodsList);
        corsConfig.setAllowedHeaders(headersList);
        corsConfig.setExposedHeaders(exposedHeadersList);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }

    private List<String> parseCommaSeparatedString(String input) {
        if (input == null || input.trim().isEmpty()) 
            return List.of();
        
        return Arrays.stream(input.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}
