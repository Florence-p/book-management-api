package com.bookmanagement.bookmanagementapp.dto;

import java.util.List;

public record AuthorDetailResponse(
        Long id,
        String name,
        String email,
        String bio,
        List<BookDetailDto> books
) {
}
