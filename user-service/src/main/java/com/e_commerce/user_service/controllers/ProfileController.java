package com.e_commerce.user_service.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.e_commerce.user_service.dtos.address.AddressDto;
import com.e_commerce.user_service.dtos.user.UpdatePasswordDto;
import com.e_commerce.user_service.dtos.user.UpdateUserDto;
import com.e_commerce.user_service.dtos.user.UserDto;
import com.e_commerce.user_service.models.User;
import com.e_commerce.user_service.res.ApiResponse;
import com.e_commerce.user_service.services.AddressService;
import com.e_commerce.user_service.services.NotificationService;
import com.e_commerce.user_service.services.UserService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;



@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    @Value("${app.name}")
    private String appName;
    
    @Autowired
    UserService userService;

    @Autowired
    AddressService addressService;

    @Autowired
    NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<UserDto>> getProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth.getName().equals("anonymousUser"))
            return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ApiResponse<>(
                    false, 
                    "Unauthorized", 
                    null
                )
            );

        User user = userService.getUserByEmail(auth.getName());
        UserDto retrieveUserDTO = new UserDto(user); 

        return  ResponseEntity.ok(
            new ApiResponse<>(
                true, 
                "User profile retrieved",
                retrieveUserDTO
            )
        );
    }

    @PutMapping("/update-profile")
    public ResponseEntity<ApiResponse<UserDto>> updateProfile(@Valid @RequestBody UpdateUserDto updateUserDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth.getName().equals("anonymousUser"))
            return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ApiResponse<>(
                    false, 
                    "Unauthorized", 
                    null
                )
            );

        User user = userService.getUserByEmail(auth.getName());
        
        return ResponseEntity.ok(
            new ApiResponse<>(
                true, 
                "Profile Updated",
                new UserDto (userService.update(updateUserDto, user))
            )
        );
    }

    @GetMapping("/address")
    public ResponseEntity<ApiResponse<List<AddressDto>>> getAllUserAddresses() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth.getName().equals("anonymousUser"))
            return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ApiResponse<>(
                    false, 
                    "Unauthorized", 
                    null
                )
            );

        User user = userService.getUserByEmail(auth.getName());
        List<Long> addresses =  user.getAddressIds();
        List<AddressDto> addressDtos = new ArrayList<AddressDto>();
        for (Long add : addresses) 
            addressDtos.add(new AddressDto(addressService.getAddressById(add)));

        return ResponseEntity.status(HttpStatus.CREATED).body(
            new ApiResponse<List<AddressDto>>(
                true, 
                "Address added successfully", 
                addressDtos
            )  
        );
    }
    
    @PostMapping("/address")
    public ResponseEntity<ApiResponse<AddressDto>> addAddress(@Valid @RequestBody AddressDto dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth.getName().equals("anonymousUser"))
            return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ApiResponse<>(
                    false, 
                    "Unauthorized", 
                    null
                )
            );

        User user = userService.getUserByEmail(auth.getName());

        return ResponseEntity.status(HttpStatus.CREATED).body(
            new ApiResponse<AddressDto>(
                true, 
                "Address added successfully", 
                new AddressDto(addressService.addAddress(dto, user))    
            )  
        );
    }

    @PutMapping("/address/{addressId}")
    public ResponseEntity<ApiResponse<AddressDto>> updateAddress(@PathVariable Long addressId, @Valid @RequestBody AddressDto dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth.getName().equals("anonymousUser"))
            return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ApiResponse<>(
                    false, 
                    "Unauthorized", 
                    null
                )
            );

        return ResponseEntity.status(HttpStatus.CREATED).body(
            new ApiResponse<AddressDto>(
                true, 
                "Address added successfully", 
                new AddressDto(addressService.UpdateAddress(addressId, dto))    
            )  
        );
    }

    @DeleteMapping("/address/{addressId}")
    public ResponseEntity<ApiResponse<AddressDto>> deleteAddress(@PathVariable Long addressId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth.getName().equals("anonymousUser"))
            return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ApiResponse<>(
                    false, 
                    "Unauthorized", 
                    null
                )
            );

        User user = userService.getUserByEmail(auth.getName());

        return ResponseEntity.status(HttpStatus.CREATED).body(
            new ApiResponse<AddressDto>(
                true, 
                "Address added successfully", 
                new AddressDto(addressService.deleteAddress(addressId, user))    
            )  
        );
    }
    

    @PutMapping("/update-password")
    public ResponseEntity<ApiResponse<UserDto>> updatePassword(@Valid @RequestBody UpdatePasswordDto updatePasswordDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth.getName().equals("anonymousUser"))
            return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ApiResponse<>(
                    false, 
                    "Unauthorized", 
                    null
                )
            );

        User user = userService.getUserByEmail(auth.getName());
        notificationService.sendPasswordChangeConfirmationEmail(user.getEmail(), user.getFirstName(), this.appName);
        
        return ResponseEntity.ok(new ApiResponse<>(
            true, 
            "Password Updated",
            new UserDto(userService.updatePassword(updatePasswordDto, user.getId()))
            )
        );
    }
}
