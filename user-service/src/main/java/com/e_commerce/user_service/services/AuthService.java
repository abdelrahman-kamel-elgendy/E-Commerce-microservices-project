package com.e_commerce.user_service.services;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.e_commerce.user_service.dto.response.UserDetailsResponse;

@FeignClient(name = "auth-service")
public interface AuthService {
    @GetMapping("/api/auth/validate-token")
    boolean validateToken(@RequestParam String token);

    @PostMapping("/api/auth/generate-verification-url")
    String generateVerificationUrl(@RequestParam String email);

    @GetMapping("/api/auth/user-details")
    UserDetailsResponse getUserDetails(@RequestParam String token);
}
