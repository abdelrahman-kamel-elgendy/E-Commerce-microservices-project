package com.e_commerce.user_service.models.Tokens;
import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "blacklisted_tokens")
public class TokenBlacklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String token;
    private Instant expiryDate;

    public TokenBlacklist(String token, Instant expiryDate) {
        this.token = token; 
        this.expiryDate = expiryDate;
    }
}