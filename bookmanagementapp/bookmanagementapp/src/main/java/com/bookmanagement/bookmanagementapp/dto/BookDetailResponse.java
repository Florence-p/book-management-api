package com.bookmanagement.bookmanagementapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDetailResponse {
    private Long id;

    private String title;

    private String isbn;

    private LocalDate publishedDate;

    private AuthorDetailDto author;

    private List<CategoryResponse> categories;

    private double rating;

    private List<ReviewResponse> reviews;
}
