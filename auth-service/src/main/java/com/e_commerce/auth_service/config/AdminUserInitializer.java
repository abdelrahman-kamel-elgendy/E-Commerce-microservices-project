package com.e_commerce.auth_service.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.e_commerce.auth_service.models.Role;
import com.e_commerce.auth_service.models.User;
import com.e_commerce.auth_service.repositories.UserRepository;

@Component
public class AdminUserInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AdminUserInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final String adminEmail;
    private final String adminPassword;
    private final String adminFirstName;
    private final String adminLastName;

    public AdminUserInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder, Environment env) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;

        this.adminEmail = env.getProperty("app.admin.user.email");
        this.adminPassword = env.getProperty("app.admin.user.password");
        this.adminFirstName = env.getProperty("app.admin.user.firstName", "Admin");
        this.adminLastName = env.getProperty("app.admin.user.lastName", "User");
    }

    @Override
    public void run(String... args) {
        try {
            createAdminUser();
        } catch (Exception e) {
            logger.error("Failed to initialize admin user: {}", e.getMessage(), e);
        }
    }

    private void createAdminUser() {
        // Basic validation
        if (adminEmail == null || adminPassword == null) {
            logger.warn("Admin credentials are not set in the environment. Skipping admin creation.");
            return;
        }

        // Check existence
        if (userRepository.existsByEmail(adminEmail)) {
            logger.info("Admin user already exists - Email: {}", adminEmail);
            return;
        }

        // Create new admin
        User adminUser = new User();
        adminUser.setEmail(adminEmail);
        adminUser.setPassword(passwordEncoder.encode(adminPassword));
        adminUser.setFirstName(adminFirstName);
        adminUser.setLastName(adminLastName);
        adminUser.setEnabled(true);
        adminUser.getRoles().add(Role.ROLE_ADMIN);

        userRepository.save(adminUser);

        logger.info("Default admin user created successfully - Email: {}", adminEmail);
        logger.info("Please change the default admin password immediately after first login.");
    }
}
