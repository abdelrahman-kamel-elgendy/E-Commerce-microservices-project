package com.e_commerce.auth_service.services;

import java.time.Duration;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.e_commerce.auth_service.dto.request.LoginUser;
import com.e_commerce.auth_service.dto.request.RegisterUser;
import com.e_commerce.auth_service.dto.request.ResetPasswordRequest;
import com.e_commerce.auth_service.dto.response.AuthResponse;
import com.e_commerce.auth_service.dto.response.NotificationResponse;
import com.e_commerce.auth_service.dto.response.UserDetailsResponse;
import com.e_commerce.auth_service.dto.response.UserResponse;
import com.e_commerce.auth_service.exceptions.InvalidCredentialsException;
import com.e_commerce.auth_service.exceptions.InvalidTokenException;
import com.e_commerce.auth_service.exceptions.UserAlreadyExistsException;
import com.e_commerce.auth_service.exceptions.UserLockedException;
import com.e_commerce.auth_service.exceptions.UserNotFoundException;
import com.e_commerce.auth_service.exceptions.ValidationException;
import com.e_commerce.auth_service.models.User;
import com.e_commerce.auth_service.models.UserSession;
import com.e_commerce.auth_service.repositories.UserRepository;
import com.e_commerce.auth_service.repositories.UserSessionRepository;
import com.e_commerce.auth_service.utils.JwtTokenProvider;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private NotificationService notificationClient;

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Value("${app.security.password-reset.reset-url}")
    private String passwordResetUrl;

    @Value("${app.security.max-sessions-per-user}")
    private int maxSessionsPerUser;

    @Value("${app.security.max-failed-login-attempts}")
    private int maxFailedLoginAttempts;

    @Value("${app.security.lock-duration-minutes}")
    private int lockDurationMinutes;

    @Value("${app.email.verification.verify-url}")
    private String verifyUrl;

    @Value("${app.jwt.expiration}")
    private Duration jwtExpiration;

    public AuthResponse authenticate(LoginUser loginUser) {
        User user = userRepository.findByEmail(loginUser.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(Instant.now()))
            // do not reveal lock timestamp to the client; instruct to check email
            throw new UserLockedException("Account locked. Check your email to unlock your account.");

        if (!passwordEncoder.matches(loginUser.getPassword(), user.getPassword())) {
            loginAttemptService.recordFailedAttempt(user.getId());
            throw new InvalidCredentialsException("Invalid email or password");
        }

        if (!user.isEnabled())
            throw new InvalidCredentialsException("Account is disabled");

        long activeSessions = userSessionRepository.countByUserEmailAndActiveTrue(user.getEmail());
        if (activeSessions >= maxSessionsPerUser)
            throw new ValidationException("Maximum session limit reached. Please logout from other devices.");

        loginAttemptService.resetFailedAttempts(user.getId());
        UserSession session = createUserSession(user);
        String token = jwtTokenProvider.generateToken(user.getEmail(), session.getSessionId());
        return new AuthResponse(token, session);
    }

    public AuthResponse refreshToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer "))
            token = token.substring(7);
        else
            throw new InvalidTokenException("token is missing");

        if (!jwtTokenProvider.validateToken(token))
            throw new InvalidTokenException("Invalid refresh token");

        String username = jwtTokenProvider.getUsernameFromToken(token);
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Long sessionId = jwtTokenProvider.getSessionIdFromToken(token);
        if (sessionId == null)
            throw new InvalidTokenException("Invalid refresh token");

        UserSession session = userSessionRepository.findBySessionIdAndActiveTrue(sessionId)
                .orElseThrow(() -> new InvalidTokenException("Invalid or expired session"));

        if (!session.getUser().getEmail().equals(user.getEmail()))
            throw new InvalidTokenException("Session does not belong to the authenticated user");

        session.setLastActivity(Instant.now());
        session.setExpiresAt(Instant.now().plus(jwtExpiration));
        userSessionRepository.save(session);
        String accessToken = jwtTokenProvider.generateToken(user.getEmail(), session.getSessionId());

        return new AuthResponse(accessToken, session);
    }

    public void resendVerification(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));
        if (user.isEnabled())
            return;

        notificationClient.sendEmailVerification(
                user.getEmail(), user.getFirstName(),
                this.generateVerificationUrl(user.getEmail()));
    }

    public UserResponse registerUser(RegisterUser registerUser) {
        if (userRepository.existsByEmail(registerUser.getEmail()))
            throw new UserAlreadyExistsException("Email already exists");

        User user = new User(
                registerUser.getEmail(),
                passwordEncoder.encode(registerUser.getPassword()),
                registerUser.getFirstName(),
                registerUser.getLastName());
        user = userRepository.save(user);

        // send verification email
        notificationClient.sendEmailVerification(
                user.getEmail(),
                user.getFirstName(),
                this.generateVerificationUrl(user.getEmail()));
        return new UserResponse(user);
    }

    public void verifyEmail(String token) {
        if (!jwtTokenProvider.validateToken(token))
            throw new InvalidTokenException("Invalid or expired token");

        String email = jwtTokenProvider.getUsernameFromToken(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setEnabled(true);
        user = userRepository.save(user);
        notificationClient.sendWelcomeEmail(email, user.getFirstName());
    }

    public void logout() {
        String token = jwtTokenProvider.resolveToken(request);
        if (token != null && jwtTokenProvider.validateToken(token)) {

            Long userId = jwtTokenProvider.getUserIdFromToken(token);
            Long sessionId = jwtTokenProvider.getSessionIdFromToken(token);

            tokenBlacklistService.blacklistToken(token, userId, sessionId, "USER_LOGOUT");

            // Update session status
            userSessionRepository.findBySessionIdAndActiveTrue(sessionId)
                    .ifPresent(session -> {
                        session.logout();
                        session.setLastActivity(Instant.now());
                        userSessionRepository.save(session);
                    });
        }
    }

    public NotificationResponse forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        return notificationClient.sendPasswordResetEmail(
                email,
                user.getFirstName(),
                passwordResetUrl + "?token=" + jwtTokenProvider.generateResetToken(user.getEmail()))
                .getBody();
    }

    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
        String token = resetPasswordRequest.getToken();
        if (!jwtTokenProvider.validateToken(token))
            throw new InvalidTokenException("Invalid or expired token");

        User user = userRepository.findByEmail(jwtTokenProvider.getUsernameFromToken(token))
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));

        if (passwordEncoder.matches(resetPasswordRequest.getNewPassword(), user.getPassword()))
            throw new ValidationException("This Password is an old password, Enter new password");

        user.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
        userRepository.save(user);
    }

    public void unlockAccount(String token) {
        if (!jwtTokenProvider.validateToken(token))
            throw new InvalidTokenException("Invalid or expired token");

        String email = jwtTokenProvider.getUsernameFromToken(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));

        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        userRepository.save(user);
    }

    private UserSession createUserSession(User user) {
        String ipAddress = getClientIpAddress();
        String userAgent = request.getHeader("User-Agent");
        Instant expiredAt = Instant.now().plus(jwtExpiration);

        UserSession userSession = new UserSession(user, expiredAt, ipAddress, userAgent);
        return userSessionRepository.save(userSession);
    }

    public boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }

    public String generateVerificationUrl(String email) {
        return verifyUrl + "?token=" + jwtTokenProvider.generateVerificationToken(email);
    }

    public String getUserEmailFromToken(String token) {
        if (!jwtTokenProvider.validateToken(token))
            throw new InvalidTokenException("Invalid or expired token");
        return jwtTokenProvider.getUsernameFromToken(token);
    }

    public UserDetailsResponse getUserDetailsFromToken(String token) {
        if (!jwtTokenProvider.validateToken(token))
            throw new InvalidTokenException("Invalid or expired token");

        String email = jwtTokenProvider.getUsernameFromToken(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return new UserDetailsResponse(
                user.getEmail(),
                user.getFirstName(),
                user.getRoles().stream().map(Enum::name).toList());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void recordFailedAttempt(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found!"));

        int attempts = user.getFailedLoginAttempts() + 1;

        user.setFailedLoginAttempts(attempts);
        if (attempts >= maxFailedLoginAttempts)
            user.setLockedUntil(Instant.now().plusSeconds(lockDurationMinutes * 60L));

        userRepository.save(user);
    }

    private String getClientIpAddress() {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null) {
            return xfHeader.split(",")[0];
        }
        return request.getRemoteAddr();
    }
}