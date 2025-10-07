package com.e_commerce.auth_service.service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.e_commerce.auth_service.dto.request.LoginRequest;
import com.e_commerce.auth_service.dto.request.SignupRequest;
import com.e_commerce.auth_service.dto.request.TokenRefreshRequest;
import com.e_commerce.auth_service.dto.response.JwtResponse;
import com.e_commerce.auth_service.dto.response.TokenRefreshResponse;
import com.e_commerce.auth_service.exceptions.RoleNotFoundException;
import com.e_commerce.auth_service.exceptions.UserAlreadyExistsException;
import com.e_commerce.auth_service.model.ERole;
import com.e_commerce.auth_service.model.RefreshToken;
import com.e_commerce.auth_service.model.Role;
import com.e_commerce.auth_service.model.User;
import com.e_commerce.auth_service.model.UserDetailsImpl;
import com.e_commerce.auth_service.repository.RoleRepository;
import com.e_commerce.auth_service.repository.UserRepository;
import com.e_commerce.auth_service.security.JwtUtils;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import com.e_commerce.auth_service.feign.NotificationClient;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private NotificationClient notificationClient;

    @Value("${app.security.email-verification.verify-url}")
    private String emailVerificationBaseUrl;

    @Transactional
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtils.generateJwtToken(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        User user = userRepository.findByEmail(userDetails.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));

        user.setLastLogin(Instant.now());

        userRepository.save(user);

        return new JwtResponse(jwt, refreshToken.getToken(), userDetails.getId(),
                userDetails.getEmail(), userDetails.getFirstName(), userDetails.getLastName(), roles);

    }

    @Transactional
    public User registerUser(SignupRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail()))
            throw new UserAlreadyExistsException("Email is already in use!");

        User user = userRepository.save(
                new User(
                        signUpRequest.getEmail(),
                        encoder.encode(signUpRequest.getPassword()),
                        signUpRequest.getFirstName(),
                        signUpRequest.getLastName()));

        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RoleNotFoundException("Role not found!"));

        user.getRoles().add(userRole);
        User saved = userRepository.save(user);

        // Send welcome email
        notificationClient.sendWelcomeEmail(
                new NotificationClient.WelcomeEmailRequest(saved.getEmail(), saved.getFirstName()));

        // Send email verification link
        String verificationUrl = String.format("%s?email=%s", emailVerificationBaseUrl, saved.getEmail());
        notificationClient.sendEmailVerification(saved.getEmail(), saved.getFirstName(), verificationUrl);

        return saved;
    }

    @Transactional
    public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {
        return new TokenRefreshResponse(
                request.getRefreshToken(),
                jwtUtils.generateTokenFromUsername(
                        refreshTokenService.verifyExpiration(
                                refreshTokenService.findByToken(
                                        request.getRefreshToken()))
                                .getUser().getEmail()));
    }

    @Transactional
    public void logoutUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        Long userId = userDetails.getId();
        refreshTokenService.deleteByUserId(userId);
    }
}