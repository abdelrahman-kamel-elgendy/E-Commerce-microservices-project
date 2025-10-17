package com.e_commerce.auth_service.services;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.e_commerce.auth_service.exceptions.UserNotFoundException;
import com.e_commerce.auth_service.models.UserSession;
import com.e_commerce.auth_service.repositories.UserRepository;
import com.e_commerce.auth_service.repositories.UserSessionRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class SessionService {

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Autowired
    private UserRepository userRepository;

    public List<UserSession> getUserSessions(String email) {
        if (!userRepository.existsByEmail(email))
            throw new UserNotFoundException("User not found");

        return userSessionRepository.findByUserEmail(email).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void logoutAllUserSessions(String email) {
        if (!userRepository.existsByEmail(email))
            throw new UserNotFoundException("User not found with email: " + email);

        List<UserSession> activeSessions = userSessionRepository.findByUserEmailAndActiveTrue(email);
        activeSessions.forEach(session -> {
            session.setActive(false);
            session.setLastActivity(Instant.now());
        });
        userSessionRepository.saveAll(activeSessions);
    }

    public long getActiveSessionCount(String email) {
        if (!userRepository.existsByEmail(email))
            throw new UserNotFoundException("User not found with email: " + email);

        return userSessionRepository.countByUserEmailAndActiveTrue(email);
    }

    private UserSession convertToDTO(UserSession entity) {
        UserSession dto = new UserSession();
        dto.setSessionId(entity.getSessionId());
        dto.setIpAddress(entity.getIpAddress());
        dto.setUserAgent(entity.getUserAgent());
        dto.setLoginTime(entity.getLoginTime());
        dto.setLastActivity(entity.getLastActivity());
        dto.setActive(entity.isActive());
        return dto;
    }
}
