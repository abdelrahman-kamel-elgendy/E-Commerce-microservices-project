package com.e_commerce.user_service.controllers;

import com.e_commerce.user_service.dto.request.AddressRequest;
import com.e_commerce.user_service.dto.request.ChangePasswordRequest;
import com.e_commerce.user_service.dto.request.UpdateProfileRequest;
import com.e_commerce.user_service.dto.response.AddressResponse;
import com.e_commerce.user_service.dto.response.UserResponse;
import com.e_commerce.user_service.services.UserService;

import jakarta.validation.Valid;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<UserResponse> me(Authentication authentication) {
        return ResponseEntity.ok(userService.getUserByEmail(authentication.getName()));
    }

    @PutMapping
    public ResponseEntity<UserResponse> updateProfile(
            Authentication authentication,
            @RequestBody UpdateProfileRequest updateProfileRequest) {
        return ResponseEntity.ok(userService.UpdateUser(authentication.getName(), updateProfileRequest));
    }

    @PutMapping("/password")
    public ResponseEntity<?> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        userService.changePassword(authentication.getName(), changePasswordRequest);
        return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
    }

    @GetMapping("/addresses")
    public ResponseEntity<Page<AddressResponse>> listAddresses(Authentication authentication, Pageable pageable) {
        return ResponseEntity.ok(userService.getAddresses(authentication.getName(), pageable));
    }

    @PostMapping("/addresses")
    public ResponseEntity<AddressResponse> addAddress(
            Authentication authentication,
            @Valid @RequestBody AddressRequest address) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.addAddress(authentication.getName(), address));
    }

    @PutMapping("/addresses/{id}")
    public ResponseEntity<AddressResponse> updateAddress(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody AddressRequest updateAddressRequest) {
        return ResponseEntity.ok(userService.updateAddress(authentication.getName(), id, updateAddressRequest));
    }

    @DeleteMapping("/addresses/{id}")
    public ResponseEntity<Void> smoothDeleteAddress(
            Authentication authentication,
            @PathVariable Long id) {
        userService.deleteAddress(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }
}