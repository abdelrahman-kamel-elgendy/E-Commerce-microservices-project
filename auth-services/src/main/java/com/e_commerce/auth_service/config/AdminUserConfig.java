package com.e_commerce.auth_service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.e_commerce.auth_service.model.ERole;
import com.e_commerce.auth_service.model.Role;
import com.e_commerce.auth_service.model.User;
import com.e_commerce.auth_service.repository.RoleRepository;
import com.e_commerce.auth_service.repository.UserRepository;

@Configuration
public class AdminUserConfig {

    @Value("${admin.user.email}")
    private String ADMIN_EMAIL;
    @Value("${admin.user.password}")
    private String ADMIN_PASSWORD;
    @Value("${admin.user.firstName}")
    private String ADMIN_FIRST_NAME;
    @Value("${admin.user.lastName}")
    private String ADMIN_LAST_NAME;

    @Autowired
    RoleRepository roleRepository;

    @Bean
    CommandLineRunner createAdminUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {

            if (!userRepository.existsByEmail(ADMIN_EMAIL)) {
                User admin = new User(
                        ADMIN_EMAIL,
                        passwordEncoder.encode(ADMIN_PASSWORD),
                        ADMIN_FIRST_NAME,
                        ADMIN_LAST_NAME);

                Role role = roleRepository.findByName(ERole.ROLE_ADMIN).get();
                if (role == null)
                    role = roleRepository.save(new Role(ERole.ROLE_ADMIN));
                admin.getRoles().add(role);

                userRepository.save(admin);
                System.out.println("Admin User Created:");
                System.out.println("Email: " + ADMIN_EMAIL);
                System.out.println("Password: " + ADMIN_PASSWORD);
            } else
                System.out.println("Admin user already exists.");

        };
    }
}