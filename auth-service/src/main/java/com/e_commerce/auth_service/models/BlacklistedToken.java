package com.e_commerce.auth_service.models;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "blacklisted_tokens")
public class BlacklistedToken {

    @Id
    private String token;

    @Column(nullable = false)
    private Instant blacklistedAt;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private String reason;

    private Long userId;

    private Long sessionId;

    public BlacklistedToken(
            String token,
            Instant blacklistedAt,
            Instant expiresAt,
            String reason,
            Long userId,
            Long sessionId) {
        this.token = token;
        this.blacklistedAt = blacklistedAt;
        this.expiresAt = expiresAt;
        this.reason = reason;
        this.userId = userId;
        this.sessionId = sessionId;
    }
}