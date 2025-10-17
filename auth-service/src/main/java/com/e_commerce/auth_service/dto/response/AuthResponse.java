package com.e_commerce.auth_service.dto.response;

import com.e_commerce.auth_service.models.UserSession;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private UserResponse user;
    private UserSessionResponse session;

    public AuthResponse(String token, UserSession userSession) {
        this.token = token;
        this.user = new UserResponse(userSession.getUser());
        this.session = new UserSessionResponse(userSession);
    }
}
