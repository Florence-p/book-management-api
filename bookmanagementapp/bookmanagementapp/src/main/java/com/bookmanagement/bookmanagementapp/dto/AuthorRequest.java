package com.bookmanagement.bookmanagementapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthorRequest(
        @NotBlank(message = "Author name is required")
        @Size(max = 150, message = "Author name must not exceed 150 characters")
        String name,
        @NotBlank(message = "Author email is required")
        @Email(message = "Author email must be valid")
        @Size(max = 150, message = "Author email must not exceed 150 characters")
        String email,
        @Size(max = 2000, message = "Author bio must not exceed 2000 characters")
        String bio
) {
}
