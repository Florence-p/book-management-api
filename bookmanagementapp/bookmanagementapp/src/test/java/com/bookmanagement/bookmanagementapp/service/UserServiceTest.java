package com.bookmanagement.bookmanagementapp.service;

import com.bookmanagement.bookmanagementapp.dto.UserCreateRequest;
import com.bookmanagement.bookmanagementapp.dto.UserUpdateRequest;
import com.bookmanagement.bookmanagementapp.entity.Role;
import com.bookmanagement.bookmanagementapp.entity.User;
import com.bookmanagement.bookmanagementapp.exception.BadRequestException;
import com.bookmanagement.bookmanagementapp.exception.DuplicateResourceException;
import com.bookmanagement.bookmanagementapp.repository.UserRepository;
import com.bookmanagement.bookmanagementapp.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, passwordEncoder, new com.bookmanagement.bookmanagementapp.util.mapper.ApiMapper());
    }

    @Test
    void createUserShouldRejectAnonymousAdminRegistration() {
        assertThatThrownBy(() -> userService.createUser(new UserCreateRequest(
                "admin1", "admin@example.com", "Password123", Role.ADMIN
        ))).isInstanceOf(BadRequestException.class)
                .hasMessage("Only an authenticated admin can create another admin user");
    }

    @Test
    void createUserShouldRejectDuplicateUsername() {
        when(userRepository.existsByUsernameIgnoreCase("reader")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(new UserCreateRequest(
                "reader", "reader@example.com", "Password123", Role.USER
        ))).isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Username already exists");
    }

    @Test
    void updateUserShouldEncodePassword() {
        User user = new User();
        user.setId(3L);
        user.setUsername("reader");
        user.setEmail("reader@example.com");
        user.setRole(Role.USER);

        when(userRepository.findById(3L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("NewPassword123")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = userService.updateUser(3L, new UserUpdateRequest(
                "reader-updated", "updated@example.com", "NewPassword123"
        ));

        assertThat(response.username()).isEqualTo("reader-updated");
        assertThat(user.getPassword()).isEqualTo("encoded-password");
    }
}
