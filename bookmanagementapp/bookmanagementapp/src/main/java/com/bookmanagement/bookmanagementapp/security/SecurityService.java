package com.bookmanagement.bookmanagementapp.security;

import com.bookmanagement.bookmanagementapp.entity.Review;
import com.bookmanagement.bookmanagementapp.entity.User;
import com.bookmanagement.bookmanagementapp.repository.ReviewRepository;
import com.bookmanagement.bookmanagementapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("securityService")
@RequiredArgsConstructor
public class SecurityService {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    public boolean canAccessUser(Long userId) {
        if (isAdmin()) {
            return true;
        }
        return userRepository.findById(userId)
                .map(User::getUsername)
                .filter(username -> username.equals(currentUsername()))
                .isPresent();
    }

    public boolean canCreateReviewForUser(Long userId) {
        return canAccessUser(userId);
    }

    public boolean canManageReview(Long reviewId) {
        if (isAdmin()) {
            return true;
        }
        return reviewRepository.findById(reviewId)
                .map(Review::getUser)
                .map(User::getUsername)
                .filter(username -> username.equals(currentUsername()))
                .isPresent();
    }

    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }

    private String currentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication == null ? null : authentication.getName();
    }
}
