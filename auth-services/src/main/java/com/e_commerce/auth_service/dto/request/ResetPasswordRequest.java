package com.e_commerce.auth_service.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {
    @NotBlank
    private String token;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 40, message = "Password must be between 6 and 40 characters")
    private String password;

    @NotBlank(message = "Password confirmation is required")
    @Size(min = 6, max = 40, message = "Password confirmation must be between 6 and 40 characters")
    private String passwordConfirmation;

    @AssertTrue(message = "Password and Password confirmation do not match!")
    public boolean isPasswordConfirmationValid() {
        return password.equals(passwordConfirmation);
    }
}
