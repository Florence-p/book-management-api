package com.bookmanagement.bookmanagementapp.dto;

import com.bookmanagement.bookmanagementapp.entity.Book;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class AuthorSummaryResponse {
    private Long id;
    private String name;
    private String email;
    private String bio;
    private List<BookSummaryDto> books;

}
