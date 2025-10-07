package com.e_commerce.auth_service.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.e_commerce.auth_service.model.ERole;
import com.e_commerce.auth_service.model.Role;
import com.e_commerce.auth_service.repository.RoleRepository;

@Configuration
public class RolesConfig {
    @Bean
    CommandLineRunner createRoles(RoleRepository roleRepository) {
        return args -> {
            if (roleRepository.findByName(ERole.ROLE_USER).isEmpty())
                roleRepository.save(new Role(ERole.ROLE_USER));

            if (roleRepository.findByName(ERole.ROLE_ADMIN).isEmpty())
                roleRepository.save(new Role(ERole.ROLE_ADMIN));

            if (roleRepository.findByName(ERole.ROLE_MODERATOR).isEmpty())
                roleRepository.save(new Role(ERole.ROLE_MODERATOR));
        };
    }
}
