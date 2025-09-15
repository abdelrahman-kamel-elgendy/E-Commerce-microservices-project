package com.e_commerce.user_service.dtos.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdatePasswordDto {
    @NotBlank(message = "Old password must not be Blank")
    private String oldPassword;

    @NotBlank(message = "Password must not be Blank")
    private String newPassword;
    
    @NotBlank(message = "Password confirmation must not be Blank")
    private String confirmPassword;    
}
