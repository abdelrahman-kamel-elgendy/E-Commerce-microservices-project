package com.e_commerce.user_service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.e_commerce.user_service.models.User;
import com.e_commerce.user_service.repositories.UserRepository;

@SpringBootApplication
@EnableFeignClients
public class UserServiceApplication {
	@Value("${admin.email}")
	private String adminEmail;
	@Value("${admin.password}")
	private String adminPassword;	
	@Value("${admin.firstName}")
	private String adminFirstName;	
	@Value("${admin.lastName}")
	private String adminLastName;	

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

	
	@Bean
    public CommandLineRunner createAdminUser(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder
    ) {
        return args -> {
            if (!userRepository.existsByEmail(adminEmail)) {
                User admin = new User();
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setRole("ADMIN");
                admin.setFirstName(adminFirstName);
                admin.setLastName(adminLastName);
                userRepository.save(admin);
                System.out.println("Admin user created! ");
            }
			else {
				System.out.println("Admin user already exists.");
			}
        };
    }

}
