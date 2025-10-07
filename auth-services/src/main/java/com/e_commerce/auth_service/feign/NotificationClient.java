package com.e_commerce.auth_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;

@FeignClient("notification-service")
public interface NotificationClient {
    @PostMapping("/api/notifications/password-reset")
    public ResponseEntity<NotificationResponse> sendPasswordResetEmail(
            @Valid @RequestBody PasswordResetEmailRequest request);

    @PostMapping("/api/notifications/welcome")
    public ResponseEntity<NotificationResponse> sendWelcomeEmail(
            @Valid @RequestBody WelcomeEmailRequest request);

    @PostMapping("/api/notifications/account-locked")
    public ResponseEntity<NotificationResponse> sendAccountLockedEmail(
            @Valid @RequestBody AccountLockedEmailRequest request);

    @PostMapping("/api/notifications/email-verification")
    public ResponseEntity<NotificationResponse> sendEmailVerification(
            @RequestParam String to,
            @RequestParam String userName,
            @RequestParam String verificationUrl);

    @Getter
    @AllArgsConstructor
    public class NotificationResponse {
        private boolean success;
        private String message;
        private String notificationId;
    }

    @Getter
    @AllArgsConstructor
    public class AccountLockedEmailRequest {
        private String to;
        private String userName;
        private String unlockUrl;
    }

    @Getter
    @AllArgsConstructor
    public class WelcomeEmailRequest {
        private String to;
        private String userName;
    }

    @Getter
    @AllArgsConstructor
    public class PasswordResetEmailRequest {
        private String to;
        private String userName;
        private String resetUrl;
        private String token;
    }

}