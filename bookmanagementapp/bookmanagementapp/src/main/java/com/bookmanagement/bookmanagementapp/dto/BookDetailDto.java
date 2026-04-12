package com.bookmanagement.bookmanagementapp.dto;

import java.time.LocalDate;

public record BookDetailDto(Long id, String title, String isbn, LocalDate publishedDate) {
}
