package com.e_commerce.user_service.dto.response;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.e_commerce.user_service.models.Role;
import com.e_commerce.user_service.models.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private List<Role> roles = new ArrayList<>();
    private Instant createdAt;
    private Instant updatedAt;

    public UserResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.roles = user.getRoles();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }
}
