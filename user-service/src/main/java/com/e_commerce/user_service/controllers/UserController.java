package com.e_commerce.user_service.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.e_commerce.user_service.dtos.user.SystemUserDto;
import com.e_commerce.user_service.dtos.user.UserDto;
import com.e_commerce.user_service.feign.NotificationServiceClient;
import com.e_commerce.user_service.models.User;
import com.e_commerce.user_service.res.ApiResponse;
import com.e_commerce.user_service.services.UserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Value("${app.name}")
    private String appName;

    @Autowired
    UserService userService;

    @Autowired
    NotificationServiceClient notificationService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserDto>> createSystemUser(@Valid @RequestBody SystemUserDto systemUserDto) {
        User user = userService.createSystemUser(systemUserDto);

        notificationService.sendWelcomeEmailForSystemCreatedUser(user.getEmail(), user.getFirstName(), appName,
                "your first name followed by @123");

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "System user created successfully",
                        new UserDto(user)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers() {
        List<User> users = userService.getAllUsers();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Users retrieved successfully",
                        users.stream()
                                .map(user -> new UserDto(user))
                                .toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "User retrieved successfully",
                        new UserDto(user)));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<UserDto>> getUserByEmail(@PathVariable String email) {
        User user = userService.getUserByEmail(email);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "User retrieved successfully",
                        new UserDto(user)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Long id) {
        Long deletedUserId = userService.deleteUser(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "User deleted successfully",
                        "Deleted user id: " + deletedUserId));
    }
}
