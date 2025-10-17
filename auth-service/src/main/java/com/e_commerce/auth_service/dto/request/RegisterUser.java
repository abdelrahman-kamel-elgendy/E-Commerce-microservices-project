package com.e_commerce.auth_service.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUser {

    @Email(message = "Invalid email format!")
    @NotBlank(message = "Email is required!")
    @Size(max = 100, message = "Email must not exceed 100 characters!")
    private String email;

    @NotBlank(message = "First name is required!")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters!")
    private String firstName;

    @NotBlank(message = "Last name is required!")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters!")
    private String lastName;

    @NotBlank(message = "Password is required!")
    @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters!")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$", message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character!")
    private String password;

    @NotBlank(message = "Password confirmation is required!")
    private String passwordConfirmation;

    @AssertTrue(message = "Password and password confirmation don't match!")
    public boolean isPasswordMatching() {
        return password != null && password.equals(passwordConfirmation);
    }
}
