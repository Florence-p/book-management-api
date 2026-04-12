package com.bookmanagement.bookmanagementapp.dto;

import com.bookmanagement.bookmanagementapp.entity.Role;

public record UserResponse(Long id, String username, String email, Role role) {
}
