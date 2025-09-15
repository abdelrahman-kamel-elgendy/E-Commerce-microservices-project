package com.e_commerce.user_service.dtos.user;

import java.time.Instant;
import java.util.List;

import com.e_commerce.user_service.models.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private boolean isActive;
    private boolean isLocked;
    private boolean enabled;
    private List<Long> addresses;
    private Instant createdAt;
    private Instant updatedAt;

    public UserDto(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.addresses = user.getAddressIds();
        this.isActive = user.isActive();
        this.isLocked = user.isLocked();
        this.enabled = user.isEnabled();
        this.createdAt = user.getCreatedAt() != null ? user.getCreatedAt() : null;
        this.updatedAt = user.getUpdatedAt() != null ? user.getUpdatedAt() : null;
    }
}
