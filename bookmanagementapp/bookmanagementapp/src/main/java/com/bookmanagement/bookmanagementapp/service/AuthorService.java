package com.bookmanagement.bookmanagementapp.service;

import com.bookmanagement.bookmanagementapp.dto.AuthorDetailResponse;
import com.bookmanagement.bookmanagementapp.dto.AuthorRequest;
import com.bookmanagement.bookmanagementapp.dto.AuthorSummaryResponse;

import java.util.List;

public interface AuthorService {

    List<AuthorSummaryResponse> getAllAuthors();

    AuthorDetailResponse getAuthorById(Long id);

    AuthorDetailResponse createAuthor(AuthorRequest request);

    AuthorDetailResponse updateAuthor(Long id, AuthorRequest request);

    void deleteAuthor(Long id);
}
