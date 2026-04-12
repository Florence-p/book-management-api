package com.bookmanagement.bookmanagementapp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record BookResponse(
        Long id,
        String title,
        String isbn,
        LocalDate publishedDate,
        AuthorSummaryDto author,
        List<CategoryResponse> categories,
        BigDecimal rating
) {
}
