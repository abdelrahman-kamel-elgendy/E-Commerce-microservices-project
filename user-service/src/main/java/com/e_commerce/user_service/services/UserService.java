package com.e_commerce.user_service.services;

import com.e_commerce.user_service.dto.request.AddressRequest;
import com.e_commerce.user_service.dto.request.ChangePasswordRequest;
import com.e_commerce.user_service.dto.request.CreateUserRequest;
import com.e_commerce.user_service.dto.request.UpdateProfileRequest;
import com.e_commerce.user_service.dto.response.AddressResponse;
import com.e_commerce.user_service.dto.response.UserResponse;
import com.e_commerce.user_service.models.User;
import com.e_commerce.user_service.models.Address;
import com.e_commerce.user_service.models.Role;
import com.e_commerce.user_service.repositories.AddressRepository;
import com.e_commerce.user_service.repositories.UserRepository;
import com.e_commerce.user_service.exceptions.BadRequestException;
import com.e_commerce.user_service.exceptions.ConflictException;
import com.e_commerce.user_service.exceptions.UserNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AuthService authService;

    public UserResponse createUser(CreateUserRequest req) {
        if (userRepository.existsByEmail(req.getEmail()))
            throw new ConflictException("Email already exists");

        User user = new User(req.getEmail(), passwordEncoder.encode(req.getFirstName() + 12345), req.getFirstName(),
                req.getLastName());

        if (req.getRoles() != null) {
            List<Role> roles = Arrays.stream(req.getRoles().split(","))
                    .map(String::trim)
                    .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                    .map(Role::valueOf)
                    .collect(Collectors.toList());
            user.setRoles(roles);
        } else {
            user.setRoles(List.of(Role.ROLE_USER));
        }
        user = userRepository.save(user);

        notificationService.sendEmailVerification(
                user.getEmail(),
                user.getFirstName(),
                authService.generateVerificationUrl(user.getEmail()));

        return new UserResponse(user);
    }

    public List<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll().stream().map(user -> new UserResponse(user)).collect(Collectors.toList());
    }

    public UserResponse getUserById(Long id) {
        return userRepository.findById(id).map(user -> new UserResponse(user))
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public UserResponse getUserByEmail(String email) {
        return userRepository.findByEmail(email).map(user -> new UserResponse(user))
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public UserResponse UpdateUser(String email, UpdateProfileRequest req) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (req.getFirstName() != null)
            user.setFirstName(req.getFirstName());
        if (req.getLastName() != null)
            user.setLastName(req.getLastName());
        user = userRepository.save(user);

        return new UserResponse(user);
    }

    public void changePassword(String email, ChangePasswordRequest changePasswordRequest) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), user.getPassword()))
            throw new BadRequestException("Current password is incorrect");

        if (changePasswordRequest.getCurrentPassword().equals(changePasswordRequest.getNewPassword()))
            throw new BadRequestException("New password must be different from current password");

        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);
    }

    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setActive(false);
        userRepository.save(user);
    }

    public Page<AddressResponse> getAddresses(String emailFromRequest, Pageable pageable) {
        User user = userRepository.findByEmail(emailFromRequest)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return addressRepository.findByUserIdAndActiveTrue(user.getId(), pageable)
                .map(address -> new AddressResponse(address));
    }

    public AddressResponse addAddress(String emailFromRequest, AddressRequest address) {
        User user = userRepository.findByEmail(emailFromRequest)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Address newAddress = new Address();
        newAddress.setUser(user);
        newAddress.setStreet(address.getStreet());
        newAddress.setCity(address.getCity());
        newAddress.setCountry(address.getCountry());
        if (address.getState() != null)
            newAddress.setState(address.getState());
        if (address.getZipCode() != null)
            newAddress.setZipCode(address.getZipCode());

        return new AddressResponse(addressRepository.save(newAddress));
    }

    public AddressResponse updateAddress(String email, Long addressId, AddressRequest updateRequest) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new UserNotFoundException("Address not found"));

        if (!address.getUser().getId().equals(user.getId()))
            throw new UserNotFoundException("Address does not belong to user");

        // Update the address fields
        address.setStreet(updateRequest.getStreet());
        address.setCity(updateRequest.getCity());
        address.setCountry(updateRequest.getCountry());

        if (updateRequest.getState() != null)
            address.setState(updateRequest.getState());

        if (updateRequest.getZipCode() != null)
            address.setZipCode(updateRequest.getZipCode());
        return new AddressResponse(addressRepository.save(address));
    }

    public void deleteAddress(String emailFromRequest, Long id) {
        User user = userRepository.findByEmail(emailFromRequest)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Address not found"));

        if (!address.getUser().getId().equals(user.getId()))
            throw new UserNotFoundException("Address does not belong to user");

        address.setActive(false);
        addressRepository.save(address);
    }
}
