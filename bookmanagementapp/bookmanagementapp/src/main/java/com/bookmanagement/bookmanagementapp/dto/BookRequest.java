package com.bookmanagement.bookmanagementapp.dto;

import com.bookmanagement.bookmanagementapp.util.validation.ValidIsbn;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;

public record BookRequest(
        @NotBlank(message = "Book title is required")
        @Size(max = 255, message = "Book title must not exceed 255 characters")
        String title,
        @NotBlank(message = "ISBN is required")
        @ValidIsbn
        String isbn,
        @NotNull(message = "Published date is required")
        LocalDate publishedDate,
        @NotNull(message = "Author id is required")
        Long authorId,
        @NotEmpty(message = "At least one category is required")
        Set<Long> categoryIds
) {
}
