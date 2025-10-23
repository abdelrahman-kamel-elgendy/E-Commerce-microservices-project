package com.e_commerce.auth_service.services;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.e_commerce.auth_service.models.BlacklistedToken;
import com.e_commerce.auth_service.repositories.BlacklistedTokenRepository;
import com.e_commerce.auth_service.utils.JwtTokenProvider;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class TokenBlacklistService {

    @Autowired
    private BlacklistedTokenRepository blacklistedTokenRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public void blacklistToken(String token, Long userId, Long sessionId, String reason) {
        if (blacklistedTokenRepository.existsByToken(token))
            return;

        Instant expiresAt = jwtTokenProvider.getExpirationDateFromToken(token).toInstant();

        // Create and save blacklisted token
        BlacklistedToken blacklistedToken = new BlacklistedToken(
                token,
                Instant.now(),
                expiresAt,
                reason != null ? reason : "LOGOUT",
                userId,
                sessionId);

        blacklistedTokenRepository.save(blacklistedToken);
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokenRepository.existsByToken(token);
    }

    @Scheduled(cron = "0 0 2 * * ?") // Run daily at 2 AM
    public void cleanupExpiredTokens() {
        Instant now = Instant.now();
        List<BlacklistedToken> expiredTokens = blacklistedTokenRepository.findByExpiresAtBefore(now);

        if (!expiredTokens.isEmpty()) {
            blacklistedTokenRepository.deleteAllExpiredBefore(now);
            // Log cleanup activity
            System.out.println("Cleaned up " + expiredTokens.size() + " expired blacklisted tokens");
        }
    }

    public long getBlacklistedTokenCount() {
        return blacklistedTokenRepository.count();
    }

    public List<BlacklistedToken> getUserBlacklistedTokens(Long userId) {
        return blacklistedTokenRepository.findByUserId(userId);
    }
}