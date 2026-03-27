package com.bookmanagement.bookmanagementapp.service;

import com.bookmanagement.bookmanagementapp.dto.BookDetailResponse;
import com.bookmanagement.bookmanagementapp.dto.BookRequest;
import com.bookmanagement.bookmanagementapp.dto.BookResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface BookService {
    Page<BookResponse> getAllBooks(Long authorId, Long categoryId, Double ratingMin,
                                   Double ratingMax, LocalDate publishedStart,
                                   LocalDate publishedEnd, Pageable pageable);
    BookDetailResponse getBookById(Long id);
    BookResponse createBook(BookRequest request);
    BookResponse updateBook(Long id, BookRequest request);
    void deleteBook(Long id);
}