package com.e_commerce.auth_service.services;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.e_commerce.auth_service.exceptions.UserNotFoundException;
import com.e_commerce.auth_service.models.User;
import com.e_commerce.auth_service.repositories.UserRepository;
import com.e_commerce.auth_service.utils.JwtTokenProvider;

@Service
public class LoginAttemptService {

    @Autowired
    private UserRepository userRepository;

    @Value("${app.security.max-failed-login-attempts}")
    private int maxFailedLoginAttempts;

    @Value("${app.security.lock-duration-minutes}")
    private int lockDurationMinutes;

    @Value("${app.security.account-unlock-url}")
    private String accountUnlockUrl;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private NotificationService notificationClient;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordFailedAttempt(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found!"));

        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);

        if (attempts >= maxFailedLoginAttempts) {
            user.setLockedUntil(Instant.now().plusSeconds(lockDurationMinutes * 60L));
            userRepository.save(user);
            notificationClient.sendAccountLockedEmail(
                    user.getEmail(),
                    user.getFirstName(),
                    accountUnlockUrl + "?token=" + jwtTokenProvider.generateUnlockToken(user.getEmail()));
            return;
        }

        userRepository.save(user);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void resetFailedAttempts(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found!"));
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        userRepository.save(user);
    }
}
