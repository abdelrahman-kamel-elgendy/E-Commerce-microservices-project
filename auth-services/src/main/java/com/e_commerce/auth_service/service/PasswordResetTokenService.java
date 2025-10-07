package com.e_commerce.auth_service.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.e_commerce.auth_service.exceptions.TokenExpiredException;
import com.e_commerce.auth_service.exceptions.TokenNotFoundException;
import com.e_commerce.auth_service.model.PasswordResetToken;
import com.e_commerce.auth_service.model.User;
import com.e_commerce.auth_service.repository.PasswordResetTokenRepository;

import io.jsonwebtoken.MalformedJwtException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PasswordResetTokenService {

    @Autowired
    PasswordResetTokenRepository tokenRepository;

    @Value("${jwt.reset-expiration}")
    private Long tokenExpirationMs;

    public PasswordResetToken createPasswordResetToken(User user) {
        // Invalidate any existing tokens for this user
        tokenRepository.markAllAsUsed(user);

        PasswordResetToken resetToken = new PasswordResetToken(
                generateToken(),
                user,
                calculateExpiryDate(),
                false);

        return tokenRepository.save(resetToken);
    }

    public PasswordResetToken validatePasswordResetToken(String token) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new TokenNotFoundException("Invalid or expired password reset token"));

        PasswordResetToken tokenEntity = resetToken;

        if (isTokenExpired(tokenEntity))
            throw new TokenExpiredException("Password reset token expired!");

        if (tokenEntity.getUsed())
            throw new MalformedJwtException("Password reset token already used");

        return tokenEntity;
    }

    @Transactional
    public void markTokenAsUsed(PasswordResetToken token) {
        token.setUsed(true);
        tokenRepository.save(token);
    }

    @Transactional
    public void invalidateAllUserTokens(User user) {
        tokenRepository.markAllAsUsed(user);
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }

    private Instant calculateExpiryDate() {
        return Instant.now().plusMillis(tokenExpirationMs);
    }

    private boolean isTokenExpired(PasswordResetToken token) {
        return token.getExpiryDate().isBefore(Instant.now());
    }

    @Transactional
    @Scheduled(fixedRate = 3600000)
    public void cleanupExpiredTokens() {
        Instant now = Instant.now();
        tokenRepository.deleteAllExpiredSince(now);
    }
}
