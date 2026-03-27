package com.bookmanagement.bookmanagementapp.service;

import com.bookmanagement.bookmanagementapp.dto.*;
import com.bookmanagement.bookmanagementapp.entity.Author;
import com.bookmanagement.bookmanagementapp.entity.Book;
import com.bookmanagement.bookmanagementapp.entity.Category;
import com.bookmanagement.bookmanagementapp.exception.ResourceNotFoundException;
import com.bookmanagement.bookmanagementapp.repository.AuthorRepository;
import com.bookmanagement.bookmanagementapp.repository.BookRepository;
import com.bookmanagement.bookmanagementapp.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public Page<BookResponse> getAllBooks(Long authorId, Long categoryId, Double ratingMin,
                                          Double ratingMax, LocalDate publishedStart,
                                          LocalDate publishedEnd, Pageable pageable) {
        return bookRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    public BookDetailResponse getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        return mapToDetailResponse(book);
    }

    @Override
    public BookResponse createBook(BookRequest request) {
        Author author = authorRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + request.getAuthorId()));

        List<Category> categories = categoryRepository.findAllById(request.getCategoryIds());
        if (categories.isEmpty()) {
            throw new ResourceNotFoundException("No valid categories found");
        }

        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setIsbn(request.getIsbn());
        book.setPublishedDate(request.getPublishedDate());
        book.setAuthor(author);
        book.setCategories(categories);
        book.setRating(0.0);

        Book saved = bookRepository.save(book);
        return mapToResponse(saved);
    }

    @Override
    public BookResponse updateBook(Long id, BookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));

        Author author = authorRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + request.getAuthorId()));

        List<Category> categories = categoryRepository.findAllById(request.getCategoryIds());

        book.setTitle(request.getTitle());
        book.setIsbn(request.getIsbn());
        book.setPublishedDate(request.getPublishedDate());
        book.setAuthor(author);
        book.setCategories(categories);

        Book updated = bookRepository.save(book);
        return mapToResponse(updated);
    }

    @Override
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        bookRepository.delete(book);
    }

    private BookResponse mapToResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .isbn(book.getIsbn())
                .publishedDate(book.getPublishedDate())
                .author(AuthorSummaryDto.builder()
                        .id(book.getAuthor().getId())
                        .name(book.getAuthor().getName())
                        .build())
                .categories(book.getCategories().stream()
                        .map(c -> CategoryResponse.builder()
                                .id(c.getId())
                                .name(c.getName())
                                .build())
                        .collect(Collectors.toList()))
                .rating(book.getRating())
                .build();
    }

    private BookDetailResponse mapToDetailResponse(Book book) {
        return BookDetailResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .isbn(book.getIsbn())
                .publishedDate(book.getPublishedDate())
                .author(AuthorDetailDto.builder()
                        .id(book.getAuthor().getId())
                        .name(book.getAuthor().getName())
                        .email(book.getAuthor().getEmail())
                        .bio(book.getAuthor().getBio())
                        .build())
                .categories(book.getCategories().stream()
                        .map(c -> CategoryResponse.builder()
                                .id(c.getId())
                                .name(c.getName())
                                .build())
                        .collect(Collectors.toList()))
                .rating(book.getRating())
                .reviews(book.getReviews().stream()
                        .map(r -> ReviewResponse.builder()
                                .id(r.getId())
                                .user(UserSummaryDto.builder()
                                        .id(r.getUser().getId())
                                        .username(r.getUser().getUsername())
                                        .build())
                                .rating(r.getRating())
                                .comment(r.getComment())
                                .createdAt(r.getCreatedAt())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}