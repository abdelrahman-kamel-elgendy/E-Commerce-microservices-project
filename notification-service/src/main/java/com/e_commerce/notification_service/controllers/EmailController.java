package com.e_commerce.notification_service.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.e_commerce.notification_service.res.ApiResponse;
import com.e_commerce.notification_service.services.EmailService;

@RestController
@RequestMapping("/api/emails")
public class EmailController {
    @Autowired
    EmailService emailService;

    @PostMapping("/welcome-email")
    public ResponseEntity<ApiResponse<?>> sendWelcomeEmail(
        @RequestParam String email,
        @RequestParam String name,
        @RequestParam String appName
    ) {
        emailService.sendWelcomeEmail(email, name, appName);
        return ResponseEntity.ok(
            new ApiResponse<>(
                true, 
                "Email sent successfully",
                null
            )
        );
    }  
    
    @PostMapping("/welcome-email-system-created-user")
    public ResponseEntity<ApiResponse<?>> sendWelcomeEmailForSystemCreatedUser(
        @RequestParam String email,
        @RequestParam String name,
        @RequestParam String appName,
        @RequestParam String password
    ) {
        emailService.sendWelcomeEmailForSystemCreatedUser(email, name, appName, password);
        return ResponseEntity.ok(
            new ApiResponse<>(
                true, 
                "Email sent successfully",
                null
            )
        );
    }

    @PostMapping("/password-reset")
    public ResponseEntity<ApiResponse<?>> sendPasswordResetEmail(
        @RequestParam String email,
        @RequestParam String name,
        @RequestParam String appName,
        @RequestParam String resetLink
    ) {
        emailService.sendPasswordResetEmail(email, name, appName, resetLink);
        return ResponseEntity.ok(
            new ApiResponse<>(
                true, 
                "Email sent successfully",
                null
            )
        );
    }

    @PostMapping("/passwerd-change-confirmation")
    public ResponseEntity<ApiResponse<?>> sendPasswordChangeConfirmationEmail(
        @RequestParam String email,
        @RequestParam String name,
        @RequestParam String appName
    ) {
        emailService.sendPasswordChangeConfirmationEmail(email, name, appName);
        return ResponseEntity.ok(
            new ApiResponse<>(
                true, 
                "Email sent successfully",
                null
            )
        );
    }
}
