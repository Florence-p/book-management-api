package com.bookmanagement.bookmanagementapp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record BookDetailResponse(
        Long id,
        String title,
        String isbn,
        LocalDate publishedDate,
        AuthorDetailDto author,
        List<CategoryResponse> categories,
        BigDecimal rating,
        List<ReviewResponse> reviews
) {
    public record AuthorDetailDto(Long id, String name, String email, String bio) {
    }
}
