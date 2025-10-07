package com.e_commerce.auth_service.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.e_commerce.auth_service.model.User;
import com.e_commerce.auth_service.model.UserDetailsImpl;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
@Component
public class JwtUtils {

        @Value("${jwt.secret}")
        private String JWT_SECRET;

        @Value("${jwt.expiration}")
        private int JWT_EXPIRATION;

        @Value("${jwt.refresh-expiration}")
        private int JWT_REFRESH_EXPIRATION;

        public String generateJwtToken(UserDetailsImpl userPrincipal) {
                return generateTokenFromUsername(userPrincipal.getEmail());
        }

        public String generateTokenFromUsername(String username) {
                return Jwts.builder()
                                .setSubject(username)
                                .setIssuedAt(new Date())
                                .setExpiration(new Date((new Date()).getTime() + JWT_EXPIRATION))
                                .signWith(key(), SignatureAlgorithm.HS256)
                                .compact();
        }

        public String generateRefreshToken(User userPrincipal) {
                return Jwts.builder()
                                .setSubject(userPrincipal.getEmail())
                                .setIssuedAt(new Date())
                                .setExpiration(new Date((new Date()).getTime() + JWT_REFRESH_EXPIRATION))
                                .signWith(key(), SignatureAlgorithm.HS256)
                                .compact();
        }

        private Key key() {
                return Keys.hmacShaKeyFor(Decoders.BASE64.decode(JWT_SECRET));
        }

        public String getUserNameFromJwtToken(String token) {
                return Jwts.parserBuilder().setSigningKey(key()).build()
                                .parseClaimsJws(token).getBody().getSubject();
        }

        public boolean validateJwtToken(String authToken) {
                Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(authToken);
                return true;
        }

        public Date getExpirationDateFromToken(String token) {
                return Jwts.parserBuilder().setSigningKey(key()).build()
                                .parseClaimsJws(token).getBody().getExpiration();
        }
}