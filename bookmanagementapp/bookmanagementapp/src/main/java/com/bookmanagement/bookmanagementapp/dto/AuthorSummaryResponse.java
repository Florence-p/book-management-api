package com.bookmanagement.bookmanagementapp.dto;

import java.util.List;

public record AuthorSummaryResponse(
        Long id,
        String name,
        String email,
        String bio,
        List<BookSummaryDto> books
) {
}
