package com.e_commerce.user_service.services;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.e_commerce.user_service.dto.response.NotificationResponse;

@FeignClient(name = "notification-service")
public interface NotificationService {
        @PostMapping("/api/notifications/email/welcome")
        public ResponseEntity<NotificationResponse> sendWelcomeEmail(
                        @RequestParam String email, @RequestParam String name);

        @PostMapping("/api/notifications/email/email-verification")
        public ResponseEntity<NotificationResponse> sendEmailVerification(
                        @RequestParam String to,
                        @RequestParam String userName,
                        @RequestParam String verificationUrl);
}
