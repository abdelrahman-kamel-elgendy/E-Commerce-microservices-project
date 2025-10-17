package com.e_commerce.auth_service.dto.response;

import java.time.Instant;

import com.e_commerce.auth_service.models.UserSession;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSessionResponse {
    private Long sessionId;
    private Instant createdAt;
    private Instant expiresAt;
    private boolean active;
    private Instant loggedOutAt;
    private Instant loginTime;
    private Instant lastActivity;
    private String ipAddress;
    private String userAgent;
    private String deviceInfo;

    public UserSessionResponse(UserSession userSession) {
        this.sessionId = userSession.getSessionId();
        this.createdAt = userSession.getCreatedAt();
        this.expiresAt = userSession.getExpiresAt();
        this.active = userSession.isActive();
        this.loggedOutAt = userSession.getLoggedOutAt();
        this.loginTime = userSession.getLoginTime();
        this.lastActivity = userSession.getLastActivity();
        this.ipAddress = userSession.getIpAddress();
        this.userAgent = userSession.getUserAgent();
        this.deviceInfo = userSession.getDeviceInfo();
    }
}
