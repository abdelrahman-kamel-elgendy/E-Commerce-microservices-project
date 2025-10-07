package com.e_commerce.notification_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountLockedEmailRequest {
    @NotBlank
    @Email
    private String to;

    @NotBlank
    private String userName;

    private String unlockUrl;
}
