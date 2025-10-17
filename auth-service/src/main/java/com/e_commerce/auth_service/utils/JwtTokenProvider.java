package com.e_commerce.auth_service.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.e_commerce.auth_service.exceptions.InvalidTokenException;
import com.e_commerce.auth_service.exceptions.TokenExpiredException;

import jakarta.servlet.http.HttpServletRequest;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private long jwtExpiration;

    @Value("${app.jwt.refresh-expiration}")
    private long jwtRefreshExpiration;

    @Value("${app.jwt.reset-expiration}")
    private long jwtResetExpiration;

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String username, Long sessionId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .setSubject(username)
                .claim("sessionId", sessionId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public String generateResetToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtResetExpiration);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .claim("purpose", "password_reset")
                .signWith(getSigningKey())
                .compact();
    }

    public String generateUnlockToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtResetExpiration);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .claim("purpose", "account_unlock")
                .signWith(getSigningKey())
                .compact();
    }

    public String generateVerificationToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtRefreshExpiration);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .claim("purpose", "email_verification")
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtRefreshExpiration);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .claim("purpose", "refresh")
                .signWith(getSigningKey())
                .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.getSubject();
    }

    public Long getSessionIdFromToken(String token) {
        Claims claims = parseClaims(token);
        Object v = claims.get("sessionId");
        if (v instanceof Number) {
            return ((Number) v).longValue();
        }
        return null;
    }

    private Claims parseClaims(String token) {
        try {
            Jws<Claims> jws = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return jws.getBody();
        } catch (ExpiredJwtException ex) {
            throw new TokenExpiredException("Expired JWT token");
        } catch (Exception ex) {
            throw new InvalidTokenException("Invalid JWT token");
        }
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (TokenExpiredException ex) {
            throw ex;
        } catch (InvalidTokenException ex) {
            throw ex;
        }
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // Additional utility methods
    public Date getExpirationDateFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.getExpiration();
    }

    public boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public Date getIssuedAtFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.getIssuedAt();
    }

    public boolean isIssuedBefore(String token, Date compareTo) {
        Date iat = getIssuedAtFromToken(token);
        if (iat == null || compareTo == null)
            return false;
        return iat.before(compareTo);
    }

    public String getTokenPurpose(String token) {
        Claims claims = parseClaims(token);
        Object p = claims.get("purpose");
        return p == null ? null : p.toString();
    }

}