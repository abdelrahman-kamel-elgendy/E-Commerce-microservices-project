package com.e_commerce.user_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.e_commerce.user_service.models.Tokens.TokenBlacklist;

public interface TokenRepository extends JpaRepository<TokenBlacklist, Long> {
    boolean existsByToken(String token);
}
