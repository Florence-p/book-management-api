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
public class BookResponse {

    private Long id;

    private String title;

    private String isbn;

    LocalDate publishedDate;

    private AuthorSummaryDto author;

    private List<CategoryResponse> categories;

    private double rating;
}
