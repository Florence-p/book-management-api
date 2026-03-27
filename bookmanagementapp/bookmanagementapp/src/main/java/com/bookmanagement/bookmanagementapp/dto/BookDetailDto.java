package com.bookmanagement.bookmanagementapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder@NoArgsConstructor
@AllArgsConstructor
public class BookDetailDto {
    private Long id;
    private String title;
    private String isbn;
    private LocalDate publishedDate;

}
