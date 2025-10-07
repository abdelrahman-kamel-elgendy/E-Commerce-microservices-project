package com.e_commerce.notification_service.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.e_commerce.notification_service.dto.AccountLockedEmailRequest;
import com.e_commerce.notification_service.dto.NotificationResponse;
import com.e_commerce.notification_service.dto.DispatchRequest;
import com.e_commerce.notification_service.dto.PasswordResetEmailRequest;
import com.e_commerce.notification_service.dto.WelcomeEmailRequest;
import com.e_commerce.notification_service.dto.PushRequest;
import com.e_commerce.notification_service.services.NotificationService;
import com.e_commerce.notification_service.services.PushService;
import com.e_commerce.notification_service.services.NotificationDispatcher;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private PushService pushService;

    @Autowired
    private NotificationDispatcher dispatcher;

    @PostMapping("/password-reset")
    public ResponseEntity<NotificationResponse> sendPasswordResetEmail(
            @Valid @RequestBody PasswordResetEmailRequest request) {
        return ResponseEntity.ok(notificationService.sendPasswordResetEmail(request));
    }

    @PostMapping("/welcome")
    public ResponseEntity<NotificationResponse> sendWelcomeEmail(
            @Valid @RequestBody WelcomeEmailRequest request) {
        return ResponseEntity.ok(notificationService.sendWelcomeEmail(request));
    }

    @PostMapping("/account-locked")
    public ResponseEntity<NotificationResponse> sendAccountLockedEmail(
            @Valid @RequestBody AccountLockedEmailRequest request) {
        return ResponseEntity.ok(notificationService.sendAccountLockedEmail(request));
    }

    @PostMapping("/email-verification")
    public ResponseEntity<NotificationResponse> sendEmailVerification(
            @RequestParam String to,
            @RequestParam String userName,
            @RequestParam String verificationUrl) {
        return ResponseEntity.ok(notificationService.sendEmailVerificationEmail(to, userName, verificationUrl));
    }

    @PostMapping("/push")
    public ResponseEntity<NotificationResponse> sendPush(@Valid @RequestBody PushRequest request) {
        return ResponseEntity.ok(pushService.sendPush(request));
    }

    @PostMapping("/send")
    public ResponseEntity<NotificationResponse> dispatch(@Valid @RequestBody DispatchRequest request) {
        return ResponseEntity.ok(dispatcher.dispatch(request));
    }
}