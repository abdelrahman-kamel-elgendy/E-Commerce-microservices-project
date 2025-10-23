package com.e_commerce.auth_service.repositories;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.e_commerce.auth_service.models.BlacklistedToken;

import feign.Param;

@Repository
public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, String> {
    boolean existsByToken(String token);

    Optional<BlacklistedToken> findBySessionId(Long sessionId);

    List<BlacklistedToken> findByExpiresAtBefore(Instant timestamp);

    long countByUserId(Long userId);

    List<BlacklistedToken> findByUserId(Long userId);

    @Modifying
    @Query("DELETE FROM BlacklistedToken b WHERE b.expiresAt < :timestamp")
    void deleteAllExpiredBefore(@Param("timestamp") Instant timestamp);
}