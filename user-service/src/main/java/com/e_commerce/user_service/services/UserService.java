package com.e_commerce.user_service.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.e_commerce.user_service.dtos.user.JwtResponseDto;
import com.e_commerce.user_service.dtos.user.LoginDto;
import com.e_commerce.user_service.dtos.user.RegisterDto;
import com.e_commerce.user_service.dtos.user.SystemUserDto;
import com.e_commerce.user_service.dtos.user.UpdatePasswordDto;
import com.e_commerce.user_service.dtos.user.UpdateUserDto;
import com.e_commerce.user_service.models.User;
import com.e_commerce.user_service.models.Tokens.PasswordResetToken;
import com.e_commerce.user_service.models.Tokens.TokenBlacklist;
import com.e_commerce.user_service.repositories.PasswordResetTokenRepository;
import com.e_commerce.user_service.repositories.TokenRepository;
import com.e_commerce.user_service.repositories.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class UserService {
    
    @Autowired
    UserRepository userRepository;
    
    @Autowired
    PasswordEncoder encoder;
    
    @Autowired
    AuthenticationManager authenticationManager;
    

    @Autowired
    TokenRepository tokenRepository;

    @Autowired  
    PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private HttpServletRequest request;

    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + id + " not found"));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with email " + email + " not found"));
    }

    public User register(RegisterDto registerDto) {
        if(!registerDto.getPassword().equals( registerDto.getPasswordConfirmation()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password confirmation does not match!");

        if(userRepository.existsByEmail(registerDto.getEmail()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is already taken!");
        
        User user = new User();
        user.setFirstName(registerDto.getFirstName());
        user.setLastName(registerDto.getLastName());
        user.setEmail(registerDto.getEmail());
        user.setPassword(encoder.encode(registerDto.getPassword()));
        user.setRole("USER");

        userRepository.save(user);
        
        return user;
    }
    
    public JwtResponseDto login(LoginDto loginDto) {
        User user = userRepository.findByEmail(loginDto.getEmail())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with email " + loginDto.getEmail() + " not found"));

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getEmail(),
                        loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateToken((UserDetails) authentication.getPrincipal());  
        
        return new JwtResponseDto(jwt, "Bearer", user.getId(), user.getEmail(), user.getFullName(), user.getRole());
    }

    public void expireToken(String headerAuth) {
        String token = headerAuth.substring(7);
        Instant expiryDate = jwtUtils.getExpirationDateFromJwtToken(token).toInstant();

        tokenRepository.save(new TokenBlacklist(token, expiryDate));
    }

    public Long deleteUser(Long id) {
        User user = this.getUserById(id);
        userRepository.delete(user);
        return id;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User createSystemUser(SystemUserDto systemUserDto) {

        if(userRepository.existsByEmail(systemUserDto.getEmail()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is already taken!");
        
        User user = new User();
        user.setFirstName(systemUserDto.getFirstName());
        user.setEmail(systemUserDto.getEmail());
        user.setPassword(encoder.encode(systemUserDto.getFirstName() + "@123"));
        user.setRole(systemUserDto.getRole());

        userRepository.save(user);
        
        return user;
    }

    public List<String> resetPasswordRequest(String email) {
        User user = this.getUserByEmail(email);
        
        String token = UUID.randomUUID().toString();
        Instant expiry = Instant.now().plusSeconds(15 * 60);
        
        passwordResetTokenRepository.save(new PasswordResetToken(email, token, expiry));
        
        // Get base URL from request
        String baseUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "");
        String resetLink = baseUrl + "/api/auth/reset-password?token=" + token;

        List<String> toReturn = new ArrayList<String>();
        toReturn.add(resetLink);
        toReturn.add(user.getFirstName());
        toReturn.add(user.getEmail());
         
        
        return toReturn;
    }

    public User resetPassword(String token, String newPassword) {
        PasswordResetToken tokenOpt = passwordResetTokenRepository.findByToken(token)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired token"));

        String email = tokenOpt.getEmail();
        User user = this.getUserByEmail(email);

        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);

        passwordResetTokenRepository.delete(tokenOpt);

        return user;
    }

    public User updatePassword(UpdatePasswordDto updatePasswordDto, Long id) {
        User user = this.getUserById(id);
            
        if(!encoder.matches(updatePasswordDto.getOldPassword(), user.getPassword())) 
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Old password is incorrect!");
            
        if(!updatePasswordDto.getNewPassword().equals(updatePasswordDto.getConfirmPassword()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password confirmation does not match!");
            
        if(encoder.matches(updatePasswordDto.getNewPassword(), user.getPassword()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New password must be different from the old password!");

        user.setPassword(encoder.encode(updatePasswordDto.getNewPassword()));

        return userRepository.save(user);        
    }

    public User update(UpdateUserDto updateUserDto, User user) {
        User existsUser;
        
        try {
            existsUser = this.getUserByEmail(updateUserDto.getEmail());
        } catch (ResponseStatusException e) {
            user.setFirstName(updateUserDto.getFitstName());
            user.setLastName(updateUserDto.getLastName());
            user.setEmail(updateUserDto.getEmail());

            return userRepository.save(user);
        }

        if(existsUser != null && existsUser.getId().equals(user.getId()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is already taken!");
        
        user.setFirstName(updateUserDto.getFitstName());
        user.setLastName(updateUserDto.getLastName());
        user.setEmail(updateUserDto.getEmail());

        return userRepository.save(user);
    }
}
