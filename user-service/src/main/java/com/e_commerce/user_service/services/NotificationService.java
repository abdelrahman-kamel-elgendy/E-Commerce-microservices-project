package com.e_commerce.user_service.services;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.e_commerce.user_service.res.ApiResponse;

@FeignClient(name = "notification-service")
public interface NotificationService {
    @PostMapping("/api/emails/welcome-email")
    public ResponseEntity<ApiResponse<?>> sendWelcomeEmail(
        @RequestParam String email,
        @RequestParam String name,
        @RequestParam String appName
    );

    @PostMapping("/api/emails/welcome-email-system-created-user")
    public ResponseEntity<ApiResponse<?>> sendWelcomeEmailForSystemCreatedUser(
        @RequestParam String email,
        @RequestParam String name,
        @RequestParam String appName,
        @RequestParam String password
    );

    @PostMapping("/api/emails/password-reset")
    public ResponseEntity<ApiResponse<?>> sendPasswordResetEmail(
        @RequestParam String email,
        @RequestParam String name,
        @RequestParam String appName,
        @RequestParam String resetLink
    );

    @PostMapping("/api/emails/passwerd-change-confirmation")
    public ResponseEntity<ApiResponse<?>> sendPasswordChangeConfirmationEmail(
        @RequestParam String email,
        @RequestParam String name,
        @RequestParam String appName
    );
}
