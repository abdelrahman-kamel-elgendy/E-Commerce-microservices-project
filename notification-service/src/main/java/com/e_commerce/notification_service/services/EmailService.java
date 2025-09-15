package com.e_commerce.notification_service.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String email, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    public void sendStandardEmail(String email, String name, String appName, String subject, String body) {
        String standardMessage = String.format(
            "Hello %s,\n\n%s\n\nBest regards,\n%s Team",
            name,
            body,
            appName
        );
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText(standardMessage);
        mailSender.send(message);
    }

    public void sendPasswordResetEmail(String userEmail, String name, String appName, String resetLink) {
        sendStandardEmail(
            userEmail,
            name,
            appName,
            "Password Reset Request",
            "We received a request to reset your password. Please click the link below to reset your password:\n" + resetLink +
            "\n\nIf you did not request this, please ignore this email. The link expires in 15 minutes."
        );
    }

    public void sendWelcomeEmail(String userEmail, String name, String appName) {
        sendStandardEmail(
            userEmail,
            name,
            appName,
            String.format("Welcome to %s!", appName),
            String.format(
                "Your account has been created successfully.\n\nThank you for joining %s!", appName)
        );
    }
    
    public void sendWelcomeEmailForSystemCreatedUser(String userEmail, String name, String appName, String password) {
        sendStandardEmail(
            userEmail,
            name,
            appName,
            String.format("Welcome to %s!", appName),
            String.format(
                "Your account has been created successfully.\n\nYou can login now with your email and your initial password is %s \n\nThank you for joining %s!",
                password, appName)
        );
    }

    public void sendPasswordChangeConfirmationEmail(String email, String name, String appName) {
        sendStandardEmail(
            email,
            name,
            appName,
            "Password Change Confirmation",
            "This is to confirm that your password has been changed successfully. If you did not make this change, please contact our support team immediately."
        );
    }
}
