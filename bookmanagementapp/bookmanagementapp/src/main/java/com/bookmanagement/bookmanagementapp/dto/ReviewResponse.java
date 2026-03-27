package com.bookmanagement.bookmanagementapp.dto;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {

    private Long id;

    private BookSummaryDto book;

    private UserSummaryDto user;

    private int rating;

    private String comment;

    private LocalDateTime createdAt;


}
