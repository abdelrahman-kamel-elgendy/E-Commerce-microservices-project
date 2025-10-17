package com.e_commerce.auth_service.services;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.e_commerce.auth_service.dto.response.NotificationResponse;

@FeignClient(name = "notification-service")
public interface NotificationService {

        @PostMapping("/api/notifications/email/password-reset")
        public ResponseEntity<NotificationResponse> sendPasswordResetEmail(
                        @RequestParam String email,
                        @RequestParam String name,
                        @RequestParam String resetUrl);

        @PostMapping("/api/notifications/email/welcome")
        public ResponseEntity<NotificationResponse> sendWelcomeEmail(
                        @RequestParam String email, @RequestParam String name);

        @PostMapping("/api/notifications/email/account-locked")
        public ResponseEntity<NotificationResponse> sendAccountLockedEmail(
                        @RequestParam String email,
                        @RequestParam String name,
                        @RequestParam String unlockLink);

        @PostMapping("/api/notifications/email/email-verification")
        public ResponseEntity<NotificationResponse> sendEmailVerification(
                        @RequestParam String to,
                        @RequestParam String userName,
                        @RequestParam String verificationUrl);
}
