package com.e_commerce.user_service.dtos.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UpdateUserDto {
    @Size(min = 3, max = 20, message = "Firstname must be from 3 to 20 chars")
    private String fitstName;
    
    @Size(min = 3, max = 20, message = "Lastname must be from 3 to 20 chars")
    private String lastName;
    
    @NotBlank(message = "Email must not be Blank")
    @Size(max = 50, message = "Email must be not more than 50 chars")
    @Email(message = "Email must be valid")
    private String email;
}
