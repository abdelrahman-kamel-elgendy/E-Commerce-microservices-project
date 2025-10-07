package com.e_commerce.notification_service.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.e_commerce.notification_service.dto.AccountLockedEmailRequest;
import com.e_commerce.notification_service.dto.EmailRequest;
import com.e_commerce.notification_service.dto.NotificationResponse;
import com.e_commerce.notification_service.dto.PasswordResetEmailRequest;
import com.e_commerce.notification_service.dto.WelcomeEmailRequest;


@Service
public class NotificationService {
    @Value("${app.email.from-name}")
    private String EMAIL_FROM_NAME;

    @Autowired
    private EmailService emailService;

    public NotificationResponse sendPasswordResetEmail(PasswordResetEmailRequest request) {
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("userName", request.getUserName());
        templateData.put("resetUrl", request.getResetUrl());
        templateData.put("token", request.getToken());
        templateData.put("appName", EMAIL_FROM_NAME);

        EmailRequest emailRequest = new EmailRequest(
                request.getTo(),
                "Password Reset Request - " + EMAIL_FROM_NAME,
                "password-reset-email",
                templateData);

        // synchronous to propagate errors to handler
        emailService.sendEmail(emailRequest);

        return new NotificationResponse(true, "Password reset email sent successfully",
                generateNotificationId(request.getTo()));
    }

    public NotificationResponse sendWelcomeEmail(WelcomeEmailRequest request) {
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("userName", request.getUserName());
        templateData.put("appName", EMAIL_FROM_NAME);

        EmailRequest emailRequest = new EmailRequest(
                request.getTo(),
                "Welcome to Our Service!",
                "welcome-email",
                templateData);

        emailService.sendEmail(emailRequest);

        return new NotificationResponse(true, "Welcome email sent successfully",
                generateNotificationId(request.getTo()));
    }

    public NotificationResponse sendAccountLockedEmail(AccountLockedEmailRequest request) {
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("userName", request.getUserName());
        templateData.put("unlockUrl", request.getUnlockUrl());
        templateData.put("appName", EMAIL_FROM_NAME);

        EmailRequest emailRequest = new EmailRequest(
                request.getTo(),
                "Account Locked - " + EMAIL_FROM_NAME,
                "account-locked-email",
                templateData);

        emailService.sendEmail(emailRequest);

        return new NotificationResponse(true, "Account locked email sent successfully",
                generateNotificationId(request.getTo()));
    }

    public NotificationResponse sendEmailVerificationEmail(String to, String userName, String verificationUrl) {
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("userName", userName);
        templateData.put("verificationUrl", verificationUrl);
        templateData.put("appName", EMAIL_FROM_NAME);

        EmailRequest emailRequest = new EmailRequest(
                to,
                "Verify Your Email - " + EMAIL_FROM_NAME,
                "email-verification-email",
                templateData);

        emailService.sendEmail(emailRequest);

        return new NotificationResponse(true, "Email verification sent successfully",
                generateNotificationId(to));
    }

    private String generateNotificationId(String recipient) {
        return "NOTIF_" + System.currentTimeMillis() + "_" + recipient.hashCode();
    }
}
