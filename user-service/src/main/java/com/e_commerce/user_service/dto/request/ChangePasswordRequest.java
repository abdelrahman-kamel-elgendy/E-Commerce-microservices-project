package com.e_commerce.user_service.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChangePasswordRequest {
    @NotBlank(message = "Current password is required!")
    private String currentPassword;

    @NotBlank(message = "Password is required!")
    @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters!")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$", message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character!")
    private String newPassword;

    @NotBlank(message = "Password confirmation is required!")
    private String passwordConfirmation;

    @AssertTrue(message = "Password and password confirmation don't match!")
    public boolean isPasswordMatching() {
        return newPassword != null && newPassword.equals(passwordConfirmation);
    }
}
