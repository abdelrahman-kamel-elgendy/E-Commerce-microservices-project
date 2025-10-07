package com.e_commerce.auth_service.repository;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.e_commerce.auth_service.model.PasswordResetToken;
import com.e_commerce.auth_service.model.User;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);

    Optional<PasswordResetToken> findByUserAndUsed(User user, Boolean used);

    @Modifying
    @Query("DELETE FROM PasswordResetToken t WHERE t.expiryDate < ?1")
    void deleteAllExpiredSince(Instant now);

    @Modifying
    @Query("UPDATE PasswordResetToken t SET t.used = true WHERE t.user = ?1 AND t.used = false")
    void markAllAsUsed(User user);
}