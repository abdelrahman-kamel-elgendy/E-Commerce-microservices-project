package com.e_commerce.auth_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.e_commerce.auth_service.dto.request.ForgotPasswordRequest;
import com.e_commerce.auth_service.dto.request.ResetPasswordRequest;
import com.e_commerce.auth_service.exceptions.InvalidCredentialsException;
import com.e_commerce.auth_service.feign.NotificationClient;
import com.e_commerce.auth_service.feign.NotificationClient.PasswordResetEmailRequest;
import com.e_commerce.auth_service.model.PasswordResetToken;
import com.e_commerce.auth_service.model.User;
import com.e_commerce.auth_service.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PasswordService {

    @Value("${app.security.password-reset.reset-url}")
    private String resetUrl;

    @Value("${app.name}")
    String APP_NAME;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenService passwordResetTokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private NotificationClient notificationClient;

    @Transactional
    public void processForgotPassword(ForgotPasswordRequest request) {
        String email = request.getEmail().toLowerCase().trim();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Password reset requested for non-existent email!"));

        if (!user.getIsEnabled()) {
            throw new InvalidCredentialsException("Password reset requested for disabled account!");
        }

        PasswordResetToken resetToken = passwordResetTokenService.createPasswordResetToken(user);

        notificationClient.sendPasswordResetEmail(new PasswordResetEmailRequest(
                email,
                user.getFirstName(),
                generateResetUrl(resetToken.getToken()),
                resetToken.getToken()));
    }

    /**
     * Generate password reset URL
     */
    private String generateResetUrl(String token) {
        return String.format("%s?token=%s", resetUrl, token);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String token = request.getToken();
        String newPassword = request.getPassword();

        PasswordResetToken resetToken = passwordResetTokenService.validatePasswordResetToken(token);
        User user = resetToken.getUser();

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        passwordResetTokenService.markTokenAsUsed(resetToken);

        refreshTokenService.deleteByUserId(user.getId());
    }
}