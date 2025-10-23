package com.e_commerce.user_service.services;

import com.e_commerce.user_service.models.User;
import com.e_commerce.user_service.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User
                .withUsername(u.getEmail())
                .password(u.getPassword())
                .authorities(mapRoles(u.getRoles()))
                .accountExpired(u.isEnabled())
                .accountLocked(!u.isEnabled())
                .credentialsExpired(!u.isEnabled())
                .disabled(!u.isEnabled())
                .build();
    }

    private Collection<? extends GrantedAuthority> mapRoles(
            java.util.List<com.e_commerce.user_service.models.Role> roles) {
        if (roles == null || roles.isEmpty())
            return List.of();
        return roles.stream()
                .map(Enum::name)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
