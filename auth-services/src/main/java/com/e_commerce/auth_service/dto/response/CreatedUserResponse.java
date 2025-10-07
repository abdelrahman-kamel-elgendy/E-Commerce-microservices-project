package com.e_commerce.auth_service.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import com.e_commerce.auth_service.model.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreatedUserResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private List<String> roles;
    private boolean active;
    private boolean locked;
    private Instant createdAt;

    public CreatedUserResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.roles = user.getRoles().stream().map(role -> role.getName().name()).collect(Collectors.toList());
        this.active = user.getIsEnabled();
        this.locked = user.getIsLocked();
        this.createdAt = user.getCreatedAt();
    }
}
