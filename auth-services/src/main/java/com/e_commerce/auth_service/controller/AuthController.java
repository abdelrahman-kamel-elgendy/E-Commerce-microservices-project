package com.e_commerce.auth_service.controller;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.e_commerce.auth_service.dto.request.ForgotPasswordRequest;
import com.e_commerce.auth_service.dto.request.LoginRequest;
import com.e_commerce.auth_service.dto.request.ResetPasswordRequest;
import com.e_commerce.auth_service.dto.request.SignupRequest;
import com.e_commerce.auth_service.dto.request.TokenRefreshRequest;
import com.e_commerce.auth_service.dto.response.JwtResponse;
import com.e_commerce.auth_service.dto.response.TokenRefreshResponse;
import com.e_commerce.auth_service.model.User;
import com.e_commerce.auth_service.service.AuthService;
import com.e_commerce.auth_service.service.PasswordResetTokenService;
import com.e_commerce.auth_service.service.PasswordService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private PasswordResetTokenService passwordResetTokenService;

    @PostMapping("/signin")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.authenticateUser(loginRequest));
    }

    @PostMapping("/signup")
    public ResponseEntity<User> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        return ResponseEntity.ok(authService.registerUser(signUpRequest));
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<TokenRefreshResponse> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        authService.logoutUser();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordService.processForgotPassword(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordService.resetPassword(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/verify-reset-token")
    public ResponseEntity<?> verifyResetToken(@Valid @RequestParam String token) {
        passwordResetTokenService.validatePasswordResetToken(token);
        return ResponseEntity.noContent().build();
    }
}