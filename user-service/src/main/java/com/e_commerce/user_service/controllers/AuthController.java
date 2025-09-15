package com.e_commerce.user_service.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.e_commerce.user_service.dtos.user.JwtResponseDto;
import com.e_commerce.user_service.dtos.user.LoginDto;
import com.e_commerce.user_service.dtos.user.RegisterDto;
import com.e_commerce.user_service.dtos.user.UserDto;
import com.e_commerce.user_service.models.User;
import com.e_commerce.user_service.res.ApiResponse;
import com.e_commerce.user_service.services.NotificationService;
import com.e_commerce.user_service.services.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Value("${app.name}")
    private String appName;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDto>> register(@Valid @RequestBody RegisterDto registerDto) {
        User user = userService.register(registerDto);
        notificationService.sendWelcomeEmail(user.getEmail(), user.getFirstName(), appName);
        
        return ResponseEntity.ok(
            new ApiResponse<UserDto>(
                true, 
                "User registered successfully", 
                new UserDto(user)
            )
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponseDto>> login(@Valid @RequestBody LoginDto loginDto) {        
        return ResponseEntity.ok(
            new ApiResponse<JwtResponseDto>(
                true, 
                "User logged in successfully", 
                userService.login(loginDto) 
            )
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(
        HttpServletRequest request
    ) {
        String headerAuth = request.getHeader("Authorization");

        if (headerAuth == null || !headerAuth.startsWith("Bearer "))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ApiResponse<>(
                    false, 
                    "No token found", 
                    null
                )
            );

        userService.expireToken(headerAuth);        
        
        return ResponseEntity.ok(
            new ApiResponse<>(
                true, 
                "Logged out successfully", 
                null
            )
        );
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<?> requestResetPassword(@RequestParam String email) {
        List<String> list = userService.resetPasswordRequest(email);

        notificationService.sendPasswordResetEmail(
            list.get(2), 
            list.get(1), 
            this.appName, 
            list.get(0)
        );

        return ResponseEntity.ok(
            new ApiResponse<>(
                true, 
                "Password reset email sent", 
                null
            )
        );
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<?>> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        User user = userService.resetPassword(token, newPassword);
        notificationService.sendPasswordChangeConfirmationEmail(user.getEmail(), user.getFirstName(), this.appName);

        return ResponseEntity.ok().body(
            new ApiResponse<>(
                true, 
                "Password updated successfully", 
                null
                )
            );
    }

}
