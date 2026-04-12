package com.bookmanagement.bookmanagementapp.service.impl;

import com.bookmanagement.bookmanagementapp.dto.UserCreateRequest;
import com.bookmanagement.bookmanagementapp.dto.UserResponse;
import com.bookmanagement.bookmanagementapp.dto.UserUpdateRequest;
import com.bookmanagement.bookmanagementapp.entity.Role;
import com.bookmanagement.bookmanagementapp.entity.User;
import com.bookmanagement.bookmanagementapp.exception.BadRequestException;
import com.bookmanagement.bookmanagementapp.exception.DuplicateResourceException;
import com.bookmanagement.bookmanagementapp.exception.ResourceNotFoundException;
import com.bookmanagement.bookmanagementapp.repository.UserRepository;
import com.bookmanagement.bookmanagementapp.service.UserService;
import com.bookmanagement.bookmanagementapp.util.mapper.ApiMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApiMapper apiMapper;

    @Override
    public UserResponse createUser(UserCreateRequest request) {
        validateRoleAssignment(request.role());
        validateDuplicates(request.username(), request.email(), null);

        User user = new User();
        user.setUsername(request.username().trim());
        user.setEmail(request.email().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(request.role());

        return apiMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        return apiMapper.toUserResponse(getUser(id));
    }

    @Override
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = getUser(id);
        validateDuplicates(request.username(), request.email(), id);

        user.setUsername(request.username().trim());
        user.setEmail(request.email().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.password()));

        return apiMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.delete(getUser(id));
    }

    private void validateDuplicates(String username, String email, Long currentUserId) {
        if (currentUserId == null) {
            if (userRepository.existsByUsernameIgnoreCase(username)) {
                throw new DuplicateResourceException("Username already exists");
            }
            if (userRepository.existsByEmailIgnoreCase(email)) {
                throw new DuplicateResourceException("Email already exists");
            }
            return;
        }
        if (userRepository.existsByUsernameIgnoreCaseAndIdNot(username, currentUserId)) {
            throw new DuplicateResourceException("Username already exists");
        }
        if (userRepository.existsByEmailIgnoreCaseAndIdNot(email, currentUserId)) {
            throw new DuplicateResourceException("Email already exists");
        }
    }

    private void validateRoleAssignment(Role requestedRole) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticatedUser = authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
        boolean isAdmin = isAuthenticatedUser && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        if (requestedRole == Role.ADMIN && !isAdmin) {
            throw new BadRequestException("Only an authenticated admin can create another admin user");
        }
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }
}
