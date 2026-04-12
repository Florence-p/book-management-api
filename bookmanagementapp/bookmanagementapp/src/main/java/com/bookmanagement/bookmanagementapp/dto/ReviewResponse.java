package com.bookmanagement.bookmanagementapp.dto;

import java.time.LocalDateTime;

public record ReviewResponse(
        Long id,
        BookSummaryDto book,
        UserSummaryDto user,
        Integer rating,
        String comment,
        LocalDateTime createdAt
) {
}
