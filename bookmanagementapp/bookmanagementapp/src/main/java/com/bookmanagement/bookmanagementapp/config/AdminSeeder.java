package com.bookmanagement.bookmanagementapp.config;

import com.bookmanagement.bookmanagementapp.entity.Role;
import com.bookmanagement.bookmanagementapp.entity.User;
import com.bookmanagement.bookmanagementapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;


    @Override
    public void run(String... args) {
        try {
            if (userRepository.existsByRole(Role.ADMIN)) {
                System.out.println("Admin already exists, skipping.");
                return;
            }

            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@bookmanagement.com");
            admin.setPassword(passwordEncoder.encode("Admin1234!"));
            admin.setRole(Role.ADMIN);

            userRepository.save(admin);
            System.out.println("Default admin created successfully.");

        } catch (Exception e) {
            System.out.println("AdminSeeder failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}