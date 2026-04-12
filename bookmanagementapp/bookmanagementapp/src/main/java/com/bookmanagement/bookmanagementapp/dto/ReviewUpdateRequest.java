package com.bookmanagement.bookmanagementapp.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReviewUpdateRequest(
        @NotNull(message = "Review rating is required")
        @Min(value = 1, message = "Review rating must be between 1 and 5")
        @Max(value = 5, message = "Review rating must be between 1 and 5")
        Integer rating,
        @NotBlank(message = "Review comment is required")
        @Size(max = 2000, message = "Review comment must not exceed 2000 characters")
        String comment
) {
}
